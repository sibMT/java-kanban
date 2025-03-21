package server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import DataTransferObject.TaskDTO;
import controllers.TaskManager;
import classes.Task;
import exception.FileManagerSaveException;
import server.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpMethod httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());

        String[] path = exchange.getRequestURI().getPath().split("/");

        switch (httpMethod) {
            case GET -> {
                if (path.length == 3) {
                    getTaskById(exchange);
                } else {
                    getAllTasks(exchange);
                }
            }
            case POST -> {
                if (path.length == 3) {
                    updateTask(exchange);
                } else {
                    addTask(exchange);
                }
            }
            case DELETE -> deleteTaskById(exchange);
        }
    }

    private void getAllTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        sendResponse(exchange, gson.toJson(tasks), HttpURLConnection.HTTP_OK);
    }

    private void getTaskById(HttpExchange exchange) throws IOException {
        Integer id = getId(exchange);

        if (id != null) {
            Task task = taskManager.getTaskById(id);

            if (task != null) {
                sendResponse(exchange, gson.toJson(task), HttpURLConnection.HTTP_OK);
            } else {
                sendResponse(exchange, convertToMessage("Задача с id=" + id + " не найдена"), HttpURLConnection.HTTP_NOT_FOUND);
            }

        } else {
            sendResponse(exchange, convertToMessage("Не верно указан id"), HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void addTask(HttpExchange exchange) throws IOException {
        if (checkHeader(exchange)) {
            InputStream inputStream = exchange.getRequestBody();
            TaskDTO taskDTO = parseTask(inputStream);

            Integer id = getId(exchange);

            if (id == null) {
                if (taskDTO != null) {
                    Task task = convertToTask(taskDTO);

                    try {
                        taskManager.createTask(task);
                        sendResponse(exchange, convertToMessage("Задача добавлена"), HttpURLConnection.HTTP_CREATED);
                    } catch (FileManagerSaveException e) {
                        sendResponse(exchange, convertToMessage(e.getMessage()), HttpURLConnection.HTTP_NOT_ACCEPTABLE);
                    }
                }
            }
        } else {
            sendResponse(exchange, convertToMessage("Неправильный формат запроса"), HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void updateTask(HttpExchange exchange) throws IOException {
        if (checkHeader(exchange)) {
            InputStream inputStream = exchange.getRequestBody();
            TaskDTO taskDTO = parseTask(inputStream);

            Integer id = getId(exchange);

            if (id != null) {
                Task task = taskManager.getTaskById(id);

                if (task != null && taskDTO != null) {
                    taskManager.updateTask(convertToTask(taskDTO, id));
                    sendResponse(exchange, convertToMessage("Задача обновлена"), HttpURLConnection.HTTP_CREATED);
                } else {
                    sendResponse(exchange, convertToMessage("Задача с id=" + id + " не найдена"), HttpURLConnection.HTTP_NOT_FOUND);
                }
            } else {
                sendResponse(exchange, convertToMessage("Не верно указан id"), HttpURLConnection.HTTP_BAD_REQUEST);
            }

        } else {
            sendResponse(exchange, convertToMessage("Неправильный формат запроса"), HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void deleteTaskById(HttpExchange exchange) throws IOException {
        Integer id = getId(exchange);

        if (id != null) {
            Task task = taskManager.getTaskById(id);

            if (task != null) {
                taskManager.removeTaskById(id);
                sendResponse(exchange, convertToMessage("Задача с id=" + id + " удалена"), HttpURLConnection.HTTP_OK);
            } else {
                sendResponse(exchange, convertToMessage("Задача с id=" + id + " не найдена"), HttpURLConnection.HTTP_NOT_FOUND);
            }

        } else {
            sendResponse(exchange, convertToMessage("Не верно указан id"), HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private TaskDTO parseTask(InputStream inputStream) throws IOException {
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(body);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return gson.fromJson(jsonObject, TaskDTO.class);
    }

    private Task convertToTask(TaskDTO taskDTO) {
        return new Task(
                taskDTO.taskName(),
                taskDTO.description(),
                taskDTO.taskStatus(),
                taskDTO.duration(),
                taskDTO.startTime()
        );
    }

    private Task convertToTask(TaskDTO taskDTO, int id) {
        return new Task(
                id,
                taskDTO.taskName(),
                taskDTO.description(),
                taskDTO.taskStatus(),
                taskDTO.duration(),
                taskDTO.startTime()
        );
    }
}
