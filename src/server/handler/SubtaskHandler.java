package server.handler;

import classes.Subtask;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import classes.Epic;
import server.HttpMethod;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            HttpMethod method = HttpMethod.valueOf(exchange.getRequestMethod());
            String[] path = exchange.getRequestURI().getPath().split("/");

            switch (method) {
                case GET -> handleGetRequest(exchange, path);
                case POST -> handlePostRequest(exchange, path);
                case DELETE -> handleDeleteRequest(exchange);
                default -> sendErrorResponse(exchange, "Method Not Allowed", 405);
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, "Internal Server Error", 500);
        }
    }

    private void handleGetRequest(HttpExchange exchange, String[] path) throws IOException {
        if (path.length >= 3) {
            getSubtaskById(exchange);
        } else {
            getAllSubtasks(exchange);
        }
    }

    private void handlePostRequest(HttpExchange exchange, String[] path) throws IOException {
        if (path.length >= 3) {
            updateSubtask(exchange);
        } else {
            createSubtask(exchange);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        deleteSubtaskById(exchange);
    }

    private void getAllSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        sendJsonResponse(exchange, subtasks, 200);
    }

    private void getSubtaskById(HttpExchange exchange) throws IOException {
        Integer id = extractId(exchange.getRequestURI());
        if (id == null) {
            sendErrorResponse(exchange, "Invalid ID", 400);
            return;
        }

        Subtask subtask = taskManager.getSubtaskById(id);
        if (subtask == null) {
            sendErrorResponse(exchange, "Subtask Not Found", 404);
            return;
        }
        sendJsonResponse(exchange, subtask, 200);
    }

    private void createSubtask(HttpExchange exchange) throws IOException {
        try {
            if (!isJsonContentType(exchange)) {
                System.err.println("Неверный Content-Type");
                sendErrorResponse(exchange, "Требуется application/json", 400);
                return;
            }

            String json = readRequestBody(exchange);
            System.out.println("Полученный JSON: " + json);

            Subtask subtask = gson.fromJson(json, Subtask.class);
            System.out.println("Парсинг Subtask: " + subtask);

            if (!isValidSubtask(subtask)) {
                System.err.println("Некорректные данные подзадачи");
                sendErrorResponse(exchange, "Некорректные данные подзадачи", 422);
                return;
            }

            System.out.println("Поиск эпика с ID: " + subtask.getEpicId());
            Epic epic = taskManager.getEpicById(subtask.getEpicId());

            if (epic == null) {
                System.err.println("Эпик не найден");
                sendErrorResponse(exchange, "Эпик не существует", 404);
                return;
            }

            System.out.println("Создание подзадачи...");
            taskManager.createSubtasks(subtask);
            System.out.println("Подзадача создана: " + subtask);

            sendJsonResponse(exchange, subtask, 201);

        } catch (JsonSyntaxException e) {
            System.err.println("Ошибка парсинга JSON: " + e.getMessage());
            sendErrorResponse(exchange, "Неверный формат JSON", 400);
        } catch (Exception e) {
            System.err.println("Серверная ошибка: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(exchange, "Внутренняя ошибка сервера", 500);
        }
    }

    private void updateSubtask(HttpExchange exchange) throws IOException {
        Integer id = extractId(exchange.getRequestURI());
        if (id == null) {
            sendErrorResponse(exchange, "Invalid ID", 400);
            return;
        }

        Subtask existingSubtask = taskManager.getSubtaskById(id);
        if (existingSubtask == null) {
            sendErrorResponse(exchange, "Subtask Not Found", 404);
            return;
        }

        Subtask updatedSubtask = parseSubtaskFromRequest(exchange);
        updatedSubtask.setId(id);

        if (taskManager.getEpicById(updatedSubtask.getEpicId()) == null) {
            sendErrorResponse(exchange, "Epic Not Found", 404);
            return;
        }

        taskManager.updateSubtask(updatedSubtask);
        sendJsonResponse(exchange, updatedSubtask, 200);
    }

    private void deleteSubtaskById(HttpExchange exchange) throws IOException {
        Integer id = extractId(exchange.getRequestURI());
        if (id == null) {
            sendErrorResponse(exchange, "Invalid ID", 400);
            return;
        }

        if (taskManager.getSubtaskById(id) == null) {
            sendErrorResponse(exchange, "Subtask Not Found", 404);
            return;
        }

        taskManager.removeSubtaskById(id);
        sendSuccessResponse(exchange, "Subtask deleted", 200);
    }

    private Subtask parseSubtaskFromRequest(HttpExchange exchange) throws IOException {
        String json = readRequestBody(exchange);
        return gson.fromJson(json, Subtask.class);
    }

    private boolean isValidSubtask(Subtask subtask) {
        return subtask != null
                && subtask.getTaskName() != null && !subtask.getTaskName().isBlank()
                && subtask.getDescription() != null && !subtask.getDescription().isBlank()
                && subtask.getTaskStatus() != null
                && subtask.getDuration() != null && subtask.getDuration().toMinutes() > 0
                && subtask.getStartTime() != null
                && subtask.getEpicId() != null;
    }

    private Integer extractId(URI uri) {
        try {
            String path = uri.getPath();
            String[] parts = path.split("/");
            return Integer.parseInt(parts[parts.length - 1]);
        } catch (Exception e) {
            return null;
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    protected void sendJsonResponse(HttpExchange exchange, Object body, int status) throws IOException {
        String response = gson.toJson(body);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void sendSuccessResponse(HttpExchange exchange, String message, int status) throws IOException {
        JsonResponse response = new JsonResponse(message);
        sendJsonResponse(exchange, response, status);
    }

    protected void sendErrorResponse(HttpExchange exchange, String message, int status) throws IOException {
        ErrorResponse error = new ErrorResponse(message, status);
        sendJsonResponse(exchange, error, status);
    }

    protected boolean isJsonContentType(HttpExchange exchange) {
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        return contentType != null && contentType.equalsIgnoreCase("application/json");
    }

    private record JsonResponse(String message) {}
    private record ErrorResponse(String error, int status) {}
}
