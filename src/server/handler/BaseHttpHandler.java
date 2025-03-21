package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import DataTransferObject.MessageDTO;
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


    protected void sendResponse(HttpExchange exchange, String text, int responseCode) throws IOException {
        byte[] responseBytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(responseCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }


    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        String response = convertToMessage(message);
        sendResponse(exchange, response, 404);
    }


    protected void sendHasInteractions(HttpExchange exchange, String message) throws IOException {
        String response = convertToMessage(message);
        sendResponse(exchange, response, 409);
    }


    protected Integer getId(HttpExchange exchange) {
        String[] path = exchange.getRequestURI().getPath().split("/");
        try {
            return Integer.parseInt(path[2]);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return null;
        }
    }


    protected boolean checkHeader(HttpExchange exchange) {
        Headers headers = exchange.getRequestHeaders();
        List<String> contentTypeValues = headers.get("Content-Type");
        return (contentTypeValues != null) && (contentTypeValues.contains("application/json"));
    }


    protected String convertToMessage(String message) {
        return gson.toJson(new MessageDTO(message));
    }
}