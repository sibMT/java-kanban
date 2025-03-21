package server.handler;


import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import classes.Task;
import server.HttpMethod;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpMethod httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());
        String[] path = exchange.getRequestURI().getPath().split("/");

        if (httpMethod.getHttpMethod().equals("GET") && path.length == 2) {
            getHistory(exchange);
        } else {
            sendResponse(exchange, convertToMessage("Неправильный формат запроса"), HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    private void getHistory(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getHistory();
        sendResponse(exchange, gson.toJson(tasks), HttpURLConnection.HTTP_OK);
    }
}
