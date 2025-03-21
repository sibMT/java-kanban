package server.handler;

import classes.Subtask;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import dto.EpicDTO;
import controllers.TaskManager;
import classes.Epic;
import server.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpMethod httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());

        String[] path = exchange.getRequestURI().getPath().split("/");

        switch (httpMethod) {
            case GET -> {
                if (path.length > 3) {
                    getEpicSubtasks(exchange);
                } else if (path.length > 2) {
                    getEpicById(exchange);
                } else {
                    getAllEpics(exchange);
                }
            }
            case POST -> addEpic(exchange);
            case DELETE -> deleteEpicById(exchange);
        }
    }

    private void getAllEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();
        sendResponse(exchange, gson.toJson(epics), HttpURLConnection.HTTP_OK);
    }

    private void getEpicById(HttpExchange exchange) throws IOException {
        Integer id = getId(exchange);

        if (id != null) {
            Epic epic = taskManager.getEpicById(id);

            if (epic != null) {
                sendResponse(exchange, gson.toJson(epic), HttpURLConnection.HTTP_OK);
            } else {
                sendResponse(exchange, convertToMessage("Эпик с id=" + id + " не найдена"), HttpURLConnection.HTTP_NOT_FOUND);
            }
        } else {
            sendResponse(exchange, convertToMessage("Не верно указан id"), HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void getEpicSubtasks(HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        String parameter = path[3];

        Integer id = getId(exchange);

        if (parameter.equals("subtasks")) {
            if (id != null) {
                Epic epic = taskManager.getEpicById(id);

                if (epic != null) {
                    List<Integer> subtaskIds = epic.getSubtasks();
                    List<Subtask> subtasks = new ArrayList<>();

                    for (Integer subtaskId : subtaskIds) {
                        Subtask subtask = taskManager.getSubtaskById(subtaskId);
                        if (subtask != null) {
                            subtasks.add(subtask);
                        }
                    }

                    if (subtasks.isEmpty()) {
                        sendResponse(exchange, convertToMessage("Список подзадач в эпике пуст"), HttpURLConnection.HTTP_NOT_FOUND);
                    } else {
                        sendResponse(exchange, gson.toJson(subtasks), HttpURLConnection.HTTP_OK);
                    }
                } else {
                    sendResponse(exchange, convertToMessage("Эпик с id=" + id + " не найдена"), HttpURLConnection.HTTP_NOT_FOUND);
                }
            } else {
                sendResponse(exchange, convertToMessage("Не верно указан id"), HttpURLConnection.HTTP_BAD_REQUEST);
            }
        } else {
            sendResponse(exchange, convertToMessage("Неправильный формат запроса"), HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void addEpic(HttpExchange exchange) throws IOException {
        if (checkHeader(exchange)) {
            InputStream inputStream = exchange.getRequestBody();
            EpicDTO epicDTO = parseEpic(inputStream); // Получаем EpicDTO

            Integer id = getId(exchange);

            if (id == null) {
                if (epicDTO != null) {
                    Epic epic = convertToEpic(epicDTO);
                    taskManager.createEpic(epic);
                    sendResponse(exchange, convertToMessage("Эпик добавлен"), HttpURLConnection.HTTP_CREATED);
                }
            } else {
                sendResponse(exchange, convertToMessage("Неправильный формат запроса"), HttpURLConnection.HTTP_BAD_REQUEST);
            }
        } else {
            sendResponse(exchange, convertToMessage("Неправильный формат запроса"), HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void deleteEpicById(HttpExchange exchange) throws IOException {
        Integer id = getId(exchange);

        if (id != null) {
            Epic epic = taskManager.getEpicById(id);

            if (epic != null) {
                taskManager.removeEpicById(id);
                sendResponse(exchange, convertToMessage("Эпик с id=" + id + " удален"), HttpURLConnection.HTTP_OK);
            } else {
                sendResponse(exchange, convertToMessage("Эпик с id=" + id + " не найден"), HttpURLConnection.HTTP_NOT_FOUND);
            }
        } else {
            sendResponse(exchange, convertToMessage("Не верно указан id"), HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private EpicDTO parseEpic(InputStream inputStream) throws IOException {
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(body);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return gson.fromJson(jsonObject, EpicDTO.class); // Возвращаем EpicDTO
    }

    private Epic convertToEpic(EpicDTO epicDto) {
        return new Epic(
                epicDto.taskName(),
                epicDto.description()
        );
    }
}

