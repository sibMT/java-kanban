package server.handler;

import classes.Subtask;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import DataTransferObject.SubtaskDTO;
import exception.FileManagerSaveException;
import controllers.TaskManager;
import classes.Epic;
import server.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpMethod httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());

        String[] path = exchange.getRequestURI().getPath().split("/");

        switch (httpMethod) {
            case GET -> {
                if (path.length == 3) {
                    getSubTaskById(exchange);
                } else {
                    getAllSubTasks(exchange);
                }
            }
            case POST -> {
                if (path.length == 3) {
                    updateSubTask(exchange);
                } else {
                    addSubTask(exchange);
                }
            }
            case DELETE -> deleteSubTaskById(exchange);
        }
    }

    private void getAllSubTasks(HttpExchange exchange) throws IOException {
        List<Subtask> subTasks = taskManager.getAllSubtasks();
        sendResponse(exchange, gson.toJson(subTasks), HttpURLConnection.HTTP_OK);
    }

    private void getSubTaskById(HttpExchange exchange) throws IOException {
        Integer id = getId(exchange);

        if (id != null) {
            Subtask subtask = taskManager.getSubtaskById(id);

            if (subtask != null) {
                sendResponse(exchange, gson.toJson(subtask), HttpURLConnection.HTTP_OK);
            } else {
                sendResponse(exchange, convertToMessage("Подзадача с id=" + id + " не найдена"), HttpURLConnection.HTTP_NOT_FOUND);
            }

        } else {
            sendResponse(exchange, convertToMessage("Не верно указан id"), HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void addSubTask(HttpExchange exchange) throws IOException {
        if (checkHeader(exchange)) {
            InputStream inputStream = exchange.getRequestBody();
            SubtaskDTO subtaskDTO = parseSubtask(inputStream);

            if (subtaskDTO != null) {
                Subtask subtask = convertToSubtask(subtaskDTO);
                Epic epic = taskManager.getEpicById(subtask.getEpicId());

                if (epic != null) {
                    try {
                        taskManager.createTask(subtask);
                        sendResponse(exchange, convertToMessage("Подзадача добавлена"), HttpURLConnection.HTTP_CREATED);
                    } catch (FileManagerSaveException e) {
                        sendResponse(exchange, convertToMessage(e.getMessage()), HttpURLConnection.HTTP_NOT_ACCEPTABLE);
                    }
                } else {
                    sendResponse(exchange, convertToMessage("Невозможно добавить подзадачу. Эпика с id=" + subtask.getEpicId() + " не существует"), HttpURLConnection.HTTP_NOT_ACCEPTABLE);
                }
            } else {
                sendResponse(exchange, convertToMessage("Неправильный формат данных подзадачи"), HttpURLConnection.HTTP_BAD_REQUEST);
            }
        } else {
            sendResponse(exchange, convertToMessage("Неправильный формат запроса"), HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void updateSubTask(HttpExchange exchange) throws IOException {
        if (checkHeader(exchange)) {
            InputStream inputStream = exchange.getRequestBody();
            SubtaskDTO subtaskDTO = parseSubtask(inputStream);

            Integer id = getId(exchange);

            if (id != null && subtaskDTO != null) {
                Subtask subtask = taskManager.getSubtaskById(id);

                if (subtask != null) {
                    Epic epic = taskManager.getEpicById(subtaskDTO.epicId());
                    if (epic != null) {
                        taskManager.updateSubtask(convertToSubtask(subtaskDTO, id));
                        sendResponse(exchange, convertToMessage("Подзадача обновлена"), HttpURLConnection.HTTP_CREATED);
                    } else {
                        sendResponse(exchange, convertToMessage("Невозможно обновить подзадачу. Эпика с id=" + subtaskDTO.epicId() + " не существует"), HttpURLConnection.HTTP_NOT_ACCEPTABLE);
                    }
                } else {
                    sendResponse(exchange, convertToMessage("Подзадача с id=" + id + " не найдена"), HttpURLConnection.HTTP_NOT_FOUND);
                }
            } else {
                sendResponse(exchange, convertToMessage("Неверно указан id или данные подзадачи"), HttpURLConnection.HTTP_BAD_REQUEST);
            }
        } else {
            sendResponse(exchange, convertToMessage("Неправильный формат запроса"), HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void deleteSubTaskById(HttpExchange exchange) throws IOException {
        Integer id = getId(exchange);

        if (id != null) {
            Subtask subtask = taskManager.getSubtaskById(id);

            if (subtask != null) {
                taskManager.removeSubtaskById(id);
                sendResponse(exchange, convertToMessage("Подзадача с id=" + id + " удалена"), HttpURLConnection.HTTP_OK);
            } else {
                sendResponse(exchange, convertToMessage("Подзадача с id=" + id + " не найдена"), HttpURLConnection.HTTP_NOT_FOUND);
            }
        } else {
            sendResponse(exchange, convertToMessage("Не верно указан id"), HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private SubtaskDTO parseSubtask(InputStream inputStream) throws IOException {
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(body);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return gson.fromJson(jsonObject, SubtaskDTO.class);
    }

    private Subtask convertToSubtask(SubtaskDTO subtaskDTO) {
        return new Subtask(
                subtaskDTO.epicId(),
                subtaskDTO.taskName(),
                subtaskDTO.description(),
                subtaskDTO.taskStatus(),
                subtaskDTO.duration(),
                subtaskDTO.startTime()
        );
    }

    private Subtask convertToSubtask(SubtaskDTO subtaskDTO, Integer id) {
        return new Subtask(
                id,
                subtaskDTO.epicId(),
                subtaskDTO.taskName(),
                subtaskDTO.description(),
                subtaskDTO.taskStatus(),
                subtaskDTO.duration(),
                subtaskDTO.startTime()
        );
    }
}
