package server.handler;

import classes.Subtask;
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


public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            HttpMethod method = HttpMethod.valueOf(exchange.getRequestMethod());
            String[] path = exchange.getRequestURI().getPath().split("/");

            switch (method) {
                case GET -> handleGet(exchange, path);
                case POST -> handlePost(exchange);
                case DELETE -> handleDelete(exchange);
                default -> sendError(exchange, "Method Not Allowed", 405);
            }
        } catch (Exception e) {
            sendError(exchange, "Internal Server Error", 500);
        }
    }

    private void handleGet(HttpExchange exchange, String[] path) throws IOException {
        if (path.length >= 4 && "subtasks".equals(path[3])) {
            getEpicSubtasks(exchange);
        } else if (path.length >= 3) {
            getEpicById(exchange);
        } else {
            getAllEpics(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        if (pathParts.length >= 3 && !pathParts[2].isEmpty()) {
            try {
                Integer.parseInt(pathParts[2]);
                updateEpic(exchange);
            } catch (NumberFormatException e) {
                sendError(exchange, "Invalid Epic ID format", 400);
            }
        } else {
            addEpic(exchange);
        }
    }

    private void addEpic(HttpExchange exchange) throws IOException {
        if (!isJsonContentType(exchange)) {
            sendError(exchange, "Content-Type must be application/json", 400);
            return;
        }

        Epic epic = parseEpicFromRequest(exchange);
        if (epic.getTaskName() == null || epic.getDescription() == null) {
            sendError(exchange, "Invalid Epic Data", 422);
            return;
        }

        taskManager.createEpic(epic);
        sendJsonResponse(exchange, epic, 201);
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        deleteEpicById(exchange);
    }

    private void getAllEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();
        sendJsonResponse(exchange, epics, 200);
    }

    private void getEpicById(HttpExchange exchange) throws IOException {
        Integer id = extractId(exchange.getRequestURI());
        if (id == null) {
            sendError(exchange, "Invalid ID", 400);
            return;
        }

        Epic epic = taskManager.getEpicById(id);
        if (epic == null) {
            sendError(exchange, "Epic Not Found", 404);
            return;
        }
        sendJsonResponse(exchange, epic, 200);
    }

    private void getEpicSubtasks(HttpExchange exchange) throws IOException {
        System.out.println("Request path: " + exchange.getRequestURI().getPath());

        Integer epicId = extractId(exchange.getRequestURI());
        System.out.println("Extracted epic ID: " + epicId);

        if (epicId == null) {
            System.out.println("Invalid epic ID");
            sendError(exchange, "Invalid Epic ID", 400);
            return;
        }

        try {
            List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epicId);
            System.out.println("Found subtasks: " + (subtasks != null ? subtasks.size() : "null"));

            if (subtasks == null) {
                sendError(exchange, "Epic Not Found", 404);
                return;
            }
            sendJsonResponse(exchange, subtasks, 200);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            sendError(exchange, "Failed to get subtasks", 500);
        }
    }

    private void updateEpic(HttpExchange exchange) throws IOException {
        Integer id = extractId(exchange.getRequestURI());
        if (id == null) {
            sendError(exchange, "Invalid ID", 400);
            return;
        }

        Epic existingEpic = taskManager.getEpicById(id);
        if (existingEpic == null) {
            sendError(exchange, "Epic Not Found", 404);
            return;
        }

        Epic updatedEpic = parseEpicFromRequest(exchange);
        updatedEpic.setId(id);
        taskManager.updateEpic(updatedEpic);
        sendJsonResponse(exchange, updatedEpic, 200);
    }

    private void deleteEpicById(HttpExchange exchange) throws IOException {
        Integer id = extractId(exchange.getRequestURI());
        if (id == null) {
            sendError(exchange, "Invalid ID", 400);
            return;
        }

        if (taskManager.getEpicById(id) == null) {
            sendError(exchange, "Epic Not Found", 404);
            return;
        }

        taskManager.removeEpicById(id);
        sendSuccessResponse(exchange, "Epic deleted", 200);
    }

    private Epic parseEpicFromRequest(HttpExchange exchange) throws IOException {
        String json = readRequestBody(exchange);
        return gson.fromJson(json, Epic.class);
    }

    private Integer extractId(URI uri) {
        try {
            String path = uri.getPath();
            String[] parts = path.split("/");
            if (parts.length >= 3 && parts[1].equals("epics")) {
                return Integer.parseInt(parts[2]);
            }
            return null;
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

    private void sendError(HttpExchange exchange, String message, int status) throws IOException {
        ErrorResponse error = new ErrorResponse(message, status);
        sendJsonResponse(exchange, error, status);
    }

    protected boolean isJsonContentType(HttpExchange exchange) {
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        return contentType != null && contentType.equalsIgnoreCase("application/json");
    }

    private record JsonResponse(String message) {
    }

    private record ErrorResponse(String error, int status) {
    }
}