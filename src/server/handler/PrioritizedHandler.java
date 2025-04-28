package server.handler;

import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import classes.Task;
import server.HttpMethod;

import java.io.IOException;
import java.util.Set;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager) {
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

            if (path.equals("/prioritized")) {
                getPrioritizedTasks(exchange);
            } else {
                sendErrorResponse(exchange, "Not Found", 404);
            }
        } catch (Exception e) {
            sendErrorResponse(exchange, "Internal Server Error", 500);
        }
    }

    private void getPrioritizedTasks(HttpExchange exchange) throws IOException {
        Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        if (prioritizedTasks.isEmpty()) {
            sendErrorResponse(exchange, "No prioritized tasks", 404);
            return;
        }
        sendJsonResponse(exchange, prioritizedTasks, 200);
    }
}
