package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.TaskManager;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;


public abstract class BaseHttpHandler implements HttpHandler {
    protected TaskManager taskManager;
    protected Gson gson;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }


    protected void sendJsonResponse(HttpExchange exchange, Object responseData, int statusCode) throws IOException {
        String response = gson.toJson(responseData);
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    protected void sendErrorResponse(HttpExchange exchange, String errorMessage, int statusCode) throws IOException {
        ErrorResponse error = new ErrorResponse(
                errorMessage,
                statusCode,
                exchange.getRequestURI().getPath()
        );
        sendJsonResponse(exchange, error, statusCode);
    }

    protected void sendStatusResponse(HttpExchange exchange, String message, int statusCode) throws IOException {
        StatusResponse response = new StatusResponse(message, statusCode);
        sendJsonResponse(exchange, response, statusCode);
    }

    protected Integer extractId(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        try {
            return Integer.parseInt(parts[parts.length - 1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    protected boolean isJsonContentType(HttpExchange exchange) {
        List<String> contentType = exchange.getRequestHeaders().get("Content-Type");
        return contentType != null &&
                contentType.stream()
                        .anyMatch(ct -> ct.equalsIgnoreCase("application/json"));
    }

    protected record ErrorResponse(String message, int status, String path) {}
    protected record StatusResponse(String message, int status) {}

}