package server.handler;

import classes.Subtask;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import classes.Task;
import exception.*;
import server.HttpMethod;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;



public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            System.out.println("Получен запрос: " + exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath());
            HttpMethod httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());
            String[] path = exchange.getRequestURI().getPath().split("/");

            switch (httpMethod) {
                case GET -> handleGetRequest(exchange, path);
                case POST -> handlePostRequest(exchange, path);
                case DELETE -> handleDeleteRequest(exchange);
                default -> sendErrorResponse(exchange, "Метод не поддерживается", 405);
            }
        } catch (TaskNotFoundException e) {
            sendErrorResponse(exchange, e.getMessage(), 404);
        } catch (ValidationException e) {
            sendErrorResponse(exchange, e.getMessage(), 400);
        } catch (Exception e) {
            sendErrorResponse(exchange, "Внутренняя ошибка сервера", 500);
        }
    }

    private void handleGetRequest(HttpExchange exchange, String[] path) throws IOException {
        if (path.length == 3) {
            getTaskById(exchange);
        } else {
            getAllTasks(exchange);
        }
    }

    private void handlePostRequest(HttpExchange exchange, String[] path) throws IOException {
        if (path.length == 3) {
            updateTask(exchange);
        } else {
            addTask(exchange);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        deleteTaskById(exchange);
    }

    private void getAllTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        sendJsonResponse(exchange, tasks, 200);
    }

    private void getTaskById(HttpExchange exchange) throws IOException {
        Integer id = extractId(exchange);
        if (id == null) {
            sendErrorResponse(exchange, "Неверный формат ID", 400);
            return;
        }

        Task task = taskManager.getTaskById(id);
        if (task == null) {
            sendErrorResponse(exchange, "Задача с id=" + id + " не найдена", 404);
            return;
        }
        sendJsonResponse(exchange, task, 200);
    }

    private void addTask(HttpExchange exchange) throws IOException {
        if (!isJsonContentType(exchange)) {
            sendErrorResponse(exchange, "Требуется application/json", 400);
            return;
        }

        try {
            String json = readRequestBody(exchange);
            System.out.println("Полученный JSON: " + json);

            Task task = gson.fromJson(json, Task.class);
            System.out.println("Десериализованная задача: " + task);

            if (!isValidTask(task)) {
                sendErrorResponse(exchange, "Некорректные данные задачи", 422);
                return;
            }

            Task createdTask = taskManager.createTask(task);
            System.out.println("Созданная задача в менеджере: " + createdTask);

            if (createdTask == null) {
                sendErrorResponse(exchange, "Не удалось создать задачу", 400);
                return;
            }

            sendJsonResponse(exchange, createdTask, 201);

        } catch (JsonSyntaxException e) {
            System.out.println("Ошибка парсинга JSON: " + e.getMessage());
            sendErrorResponse(exchange, "Неверный формат JSON", 400);
        } catch (Exception e) {
            System.out.println("Ошибка при создании задачи: " + e.getMessage());
            sendErrorResponse(exchange, "Внутренняя ошибка сервера", 500);
        }
    }

    private void updateTask(HttpExchange exchange) throws IOException {
        if (!isJsonContentType(exchange)) {
            sendErrorResponse(exchange, "Требуется application/json", 400);
            return;
        }

        Integer id = extractId(exchange);
        if (id == null) {
            sendErrorResponse(exchange, "Неверный формат ID", 400);
            return;
        }

        try {
            Task existingTask = taskManager.getTaskById(id);
            if (existingTask == null) {
                sendErrorResponse(exchange, "Задача не найдена", 404);
                return;
            }

            Task updatedTask = parseTaskFromRequest(exchange);
            updatedTask.setId(id);

            if (!isValidTask(updatedTask)) {
                sendErrorResponse(exchange, "Некорректные данные задачи", 422);
                return;
            }

            taskManager.updateTask(updatedTask);
            sendJsonResponse(exchange, updatedTask, 200);

        } catch (JsonSyntaxException e) {
            sendErrorResponse(exchange, "Неверный формат JSON", 400);
        }
    }

    private void deleteTaskById(HttpExchange exchange) throws IOException {
        Integer id = extractId(exchange);
        if (id == null) {
            sendErrorResponse(exchange, "Неверный формат ID", 400);
            return;
        }

        Task task = taskManager.getTaskById(id);
        if (task == null) {
            sendErrorResponse(exchange, "Задача не найдена", 404);
            return;
        }

        taskManager.removeTaskById(id);
        sendStatusResponse(exchange, "Задача удалена", 200);
    }

    private Task parseTaskFromRequest(HttpExchange exchange) throws IOException {
        String json = readRequestBody(exchange);
        return gson.fromJson(json, Task.class);
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private boolean isValidTask(Task task) {
        if (task == null) {
            System.out.println("Задача null");
            return false;
        }

        if (task.getTaskName() == null || task.getTaskName().isBlank()) {
            System.out.println("Отсутствует название задачи");
            return false;
        }

        if (task.getTaskStatus() == null) {
            System.out.println("Отсутствует статус задачи");
            return false;
        }

        if (task.getStartTime() != null) {
            if (task.getDuration() == null || task.getDuration().isNegative()) {
                System.out.println("Некорректная длительность");
                return false;
            }
        }

        if(hasConflict(task)) {
            throw new ValidationException("Пересечение по времени","Код ошибки 406");
        }
        return true;
    }

    private boolean hasConflict(Task task) {
        if(task.getStartTime() == null) {
            return false;
        }
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();

        for(Task newTask : taskManager.getAllTasks()) {
            if(newTask.getId() != task.getId() &&
            newTask.getStartTime() != null) {

                LocalDateTime newStart = newTask.getStartTime();
                LocalDateTime newEnd = newTask.getEndTime();

                if(!(end.isBefore(newStart) || start.isAfter(newEnd))) {
                    System.out.println("Пересечение с задачей ID=" + newTask.getId());
                    return true;
                }
            }
        }
        for(Subtask newSubtask : taskManager.getAllSubtasks()) {
            if(newSubtask.getId() != task.getId() &&
            newSubtask.getStartTime() != null) {
                LocalDateTime newStart = newSubtask.getStartTime();
                LocalDateTime newEnd = newSubtask.getEndTime();

                if(!(end.isBefore(newStart) || start.isAfter(newEnd))) {
                    System.out.println("Пересечение с подзадачей ID=" + newSubtask.getId());
                    return true;
                }
            }
        }
        return false;
    }
}


