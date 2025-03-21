package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import classes.Task;
import server.HttpMethod;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Set;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpMethod httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());
        String[] path = exchange.getRequestURI().getPath().split("/");

        if (httpMethod.getHttpMethod().equals("GET") && path.length == 2) {
            getPrioritizedTasks(exchange);
        } else {
            sendResponse(exchange, convertToMessage("Неправильный формат запроса"), HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void getPrioritizedTasks(HttpExchange exchange) throws IOException {
        Set<Task> tasks = taskManager.getPrioritizedTasks();
        sendResponse(exchange, gson.toJson(tasks), HttpURLConnection.HTTP_OK);
    }
}
