package server.handler;


import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import classes.Task;
import server.HttpMethod;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            HttpMethod method = HttpMethod.valueOf(exchange.getRequestMethod());
            String path = exchange.getRequestURI().getPath();

            if (method != HttpMethod.GET) {
                sendErrorResponse(exchange, "Method Not Allowed", 405);
                return;
            }

            if (path.equals("/history")) {
                getHistory(exchange);
            } else {
                sendErrorResponse(exchange, "Not Found", 404);
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, "Internal Server Error", 500);
        }
    }

    private void getHistory(HttpExchange exchange) throws IOException {
        List<Task> history = taskManager.getHistory();
        if (history.isEmpty()) {
            sendErrorResponse(exchange, "History is empty", 404);
            return;
        }
        sendJsonResponse(exchange, history, 200);
    }
}
