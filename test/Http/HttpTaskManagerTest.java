package Http;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import adapter.TaskStatusAdapter;
import classes.Epic;
import classes.Subtask;
import classes.Task;
import classes.TaskStatus;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import controllers.HistoryManager;
import controllers.InMemoryHistoryManager;
import controllers.InMemoryTaskManager;
import controllers.TaskManager;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest {

    private static final String HOSTNAME = "localhost";
    private static final int PORT = 8080;
    private static HttpTaskServer taskServer;
    private static TaskManager manager;
    private static Gson gson;
    private final HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    public void setUp() throws IOException {
        HistoryManager historyManager = new InMemoryHistoryManager();
        manager = new InMemoryTaskManager(historyManager);
        taskServer = new HttpTaskServer(PORT, HOSTNAME, manager);
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(TaskStatus.class, new TaskStatusAdapter())
                .create();
        taskServer.start();
        resetManager();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop(0);
    }

    public void resetManager() {
        manager.removeAllTasks();
        manager.removeAllEpics();
        manager.removeAllSubtasks();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task(
                "Task1",
                "Task description",
                TaskStatus.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
        );
        String json = gson.toJson(task);
        System.out.println("Отправляемый JSON: " + json);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        System.out.println("Отправка запроса: " + request.uri());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Получен ответ: " + response.statusCode() + " " + response.body());

        assertEquals(201, response.statusCode(), "Статус код должен быть 201");
        assertEquals(1, manager.getAllTasks().size(), "Некорректное количество задач");
        assertNotNull(manager.getAllTasks(), "Список задач не должен быть null");

        Task createdTask = manager.getTaskById(1);
        assertEquals("Task1", createdTask.getTaskName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task(
                "Test Task",
                "Description",
                TaskStatus.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 6, 1, 10, 0)
        );

        String createJson = gson.toJson(task);
        System.out.println("Create JSON: " + createJson);

        HttpResponse<String> createResponse = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks"))
                        .POST(HttpRequest.BodyPublishers.ofString(createJson))
                        .header("Content-Type", "application/json")
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(201, createResponse.statusCode());
        Task createdTask = gson.fromJson(createResponse.body(), Task.class);
        int taskId = createdTask.getId();

        Task updatedTask = new Task(
                "Updated Task",
                "New Description",
                TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(45),
                LocalDateTime.of(2024, 6, 1, 11, 0)
        );
        updatedTask.setId(taskId);

        String updateJson = gson.toJson(updatedTask);
        System.out.println("Update JSON: " + updateJson);

        HttpRequest updateRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(updateJson))
                .build();

        HttpResponse<String> updateResponse = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status code: " + updateResponse.statusCode());
        System.out.println("Response body: " + updateResponse.body());

        assertEquals(200, updateResponse.statusCode(), "Ошибка при обновлении задачи");

        Task actualTask = manager.getTaskById(taskId);
        assertNotNull(actualTask);
        assertEquals("Updated Task", actualTask.getTaskName());
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task(
                "Task1",
                "Task description",
                TaskStatus.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
        );
        manager.createTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int expectedStatusCode = 200;
        int actuallyStatusCode = response.statusCode();
        assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 200");

        String expectedBody = "[{\"id\":1,\"taskName\":\"Task1\",\"description\":\"Task description\",\"taskStatus\":" +
                "\"NEW\",\"duration\":5,\"startTime\":\"2025-02-28T09:00:00\",\"endTime\":\"2025-02-28T09:05:00\"}]";
        String actuallyBody = response.body();
        assertEquals(expectedBody, actuallyBody, "Ответ не совпадает с ожидаемым");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task(
                "Task1",
                "Task description",
                TaskStatus.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
        );
        manager.createTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int expectedStatusCode = 200;
        int actuallyStatusCode = response.statusCode();
        assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 200");

        String expectedBody = "{\"id\":1,\"taskName\":\"Task1\",\"description\":\"Task description\",\"taskStatus\":\"" +
                "NEW\",\"duration\":5,\"startTime\":\"2025-02-28T09:00:00\",\"endTime\":\"2025-02-28T09:05:00\"}";
        String actuallyBody = response.body();
        assertEquals(expectedBody, actuallyBody, "Ответ не совпадает с ожидаемым");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW);

        String json = gson.toJson(task);
        System.out.println("Отправляемый JSON: " + json);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Ошибка при создании задачи");
        Task createdTask = gson.fromJson(response.body(), Task.class);

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + createdTask.getId()))
                .DELETE()
                .build();

        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteResponse.statusCode(), "Ошибка при удалении задачи");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epicDto = new Epic("Epic1", "Epic1 description");

        String json = gson.toJson(epicDto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int expectedStatusCode = 201;
        int expectedEpicsSize = 1;
        String expectedEpicName = "Epic1";
        int actuallyStatusCode = response.statusCode();
        int actuallyEpicsSize = manager.getAllEpics().size();
        String actuallyEpicsName = manager.getAllEpics().getFirst().getTaskName();

        Assertions.assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 201");
        Assertions.assertEquals(expectedEpicsSize, actuallyEpicsSize, "Некорректное количество эпиков");
        Assertions.assertEquals(expectedEpicName, actuallyEpicsName, "Некорректное имя задачи");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic(
                "Epic1",
                "Epic1 description",
                Duration.ofMinutes(0),
                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
        );

        manager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int expectedStatusCode = 200;
        String expectedBody = "[{\"subtasks\":[],\"id\":1,\"taskName\":\"Epic1\",\"description\":\"Epic1 description\"," +
                "\"taskStatus\":\"NEW\",\"duration\":0,\"startTime\":\"2025-02-28T09:00:00\",\"endTime\":\"2025-02-28T09:00:00\"}]";
        int actuallyStatusCode = response.statusCode();
        String actuallyBody = response.body();

        assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 200");
        assertEquals(expectedBody, actuallyBody, "Ответ не совпадает с ожидаемым");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic(
                "Epic1",
                "Epic1 description",
                Duration.ofMinutes(0),
                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
        );

        manager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int expectedStatusCode = 200;
        String expectedBody = "{\"subtasks\":[],\"id\":1,\"taskName\":\"Epic1\",\"description\":\"Epic1 description\"," +
                "\"taskStatus\":\"NEW\",\"duration\":0,\"startTime\":\"2025-02-28T09:00:00\",\"endTime\":\"2025-02-28T09:00:00\"}";
        int actuallyStatusCode = response.statusCode();
        String actuallyBody = response.body();

        assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 200");
        assertEquals(expectedBody, actuallyBody, "Ответ не совпадает с ожидаемым");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic originalEpic = new Epic(
                "Original Epic",
                "Original epic description",
                Duration.ofMinutes(0),
                LocalDateTime.of(LocalDate.of(2025, 3, 1), LocalTime.of(9, 0))
        );
        manager.createEpic(originalEpic);
        int epicId = originalEpic.getId();

        Epic updatedEpic = new Epic(
                "Updated Epic",
                "Updated epic description",
                Duration.ofMinutes(0),
                LocalDateTime.of(LocalDate.of(2025, 3, 2), LocalTime.of(10, 0))
        );
        updatedEpic.setId(epicId);

        String json = gson.toJson(updatedEpic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус код при обновлении эпика");

        Epic actualEpic = manager.getEpicById(epicId);
        assertNotNull(actualEpic, "Эпик не найден после обновления");
        assertEquals("Updated Epic", actualEpic.getTaskName(), "Имя не обновилось");
        assertEquals("Updated epic description", actualEpic.getDescription(), "Описание не обновилось");
        assertEquals(LocalDateTime.of(LocalDate.of(2025, 3, 2), LocalTime.of(10, 0)),
                actualEpic.getStartTime(), "Время начала не обновилось");
    }


    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Epic1 description");
        manager.createEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask(
                epicId,
                "Subtask1",
                "Subtask description",
                TaskStatus.NEW,
                Duration.ofMinutes(10),
                LocalDateTime.of(2025, 2, 28, 9, 0)
        );
        manager.createSubtasks(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус код");

        JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
        assertFalse(jsonArray.isEmpty(), "Список подзадач не должен быть пуст");

        JsonObject subtaskJson = jsonArray.get(0).getAsJsonObject();
        assertEquals("Subtask1", subtaskJson.get("taskName").getAsString());
        assertEquals(epicId, subtaskJson.get("epicId").getAsInt());
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic(
                "Epic1",
                "Epic1 description",
                Duration.ofMinutes(0),
                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
        );

        manager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int expectedStatusCode = 200;
        int expectedEpicsSize = 0;
        int actuallyStatusCode = response.statusCode();
        int actuallyEpicsSize = manager.getAllTasks().size();

        assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 200");
        assertEquals(expectedEpicsSize, actuallyEpicsSize, "Менеджере задач не пуст");
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic(
                "Epic1",
                "Epic1 description",
                Duration.ofMinutes(0),
                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
        );

        manager.createEpic(epic);

        Subtask subtaskDto = new Subtask(
                1,
                "Subtask1",
                "Subtask1 description",
                TaskStatus.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
        );

        String json = gson.toJson(subtaskDto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int expectedStatusCode = 201;
        int expectedEpicsSize = 1;
        String expectedSubtaskName = "Subtask1";
        int actuallyStatusCode = response.statusCode();
        int actuallySubtasksSize = manager.getAllSubtasks().size();
        String actuallySubtaskName = manager.getAllSubtasks().getFirst().getTaskName();

        Assertions.assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 201");
        Assertions.assertEquals(expectedEpicsSize, actuallySubtasksSize, "Некорректное количество подзадач");
        Assertions.assertEquals(expectedSubtaskName, actuallySubtaskName, "Некорректное имя задачи");
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic(
                "Epic1",
                "Epic1 description",
                Duration.ofMinutes(0),
                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
        );
        manager.createEpic(epic);
        Subtask subtask = new Subtask(
                epic,
                "Subtask1",
                "Subtask description",
                TaskStatus.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
        );
        manager.createSubtasks(subtask);
        Subtask subtask1 = new Subtask(
                1,
                "Subtask1 updated",
                "Updated description",
                TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(10),
                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(10, 0))
        );
        subtask1.setId(subtask.getId());

        String json = gson.toJson(subtask1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtask.getId()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус код при обновлении");
        Subtask realSubtask = manager.getSubtaskById(subtask.getId());
        assertNotNull(realSubtask, "Подзадача не найдена после обновления");
        assertEquals("Subtask1 updated", realSubtask.getTaskName(), "Имя не обновилось");
        assertEquals("Updated description", realSubtask.getDescription(), "Описание не обновилось");
        assertEquals(TaskStatus.IN_PROGRESS, realSubtask.getTaskStatus(), "Статус не обновился");
        assertEquals(Duration.ofMinutes(10), realSubtask.getDuration(), "Длительность не обновилась");
        assertEquals(LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(10, 0)),
                realSubtask.getStartTime(), "Время начала не обновилось");
        assertEquals(1, manager.getAllSubtasks().size(), "Количество подзадач изменилось");
    }

    @Test
    public void testGetSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic(
                "Epic1",
                "Epic1 description",
                Duration.ofMinutes(0),
                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
        );

        manager.createEpic(epic);

        Subtask subtask = new Subtask(
                epic,
                "Subtask1",
                "Subtask description",
                TaskStatus.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
        );

        manager.createSubtasks(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int expectedStatusCode = 200;
        String expectedBody = "[{\"epicId\":1,\"id\":2,\"taskName\":\"Subtask1\",\"description\":\"Subtask description\"," +
                "\"taskStatus\":\"NEW\",\"duration\":5,\"startTime\":\"2025-02-28T09:00:00\",\"endTime\":\"2025-02-28T09:05:00\"}]";
        int actuallyStatusCode = response.statusCode();
        String actuallyBody = response.body();

        assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 200");
        assertEquals(expectedBody, actuallyBody, "Ответ не совпадает с ожидаемым");
    }

    @Test
    public void testGetSubTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic(
                "Epic1",
                "Epic1 description",
                Duration.ofMinutes(0),
                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
        );

        manager.createEpic(epic);

        Subtask subTask = new Subtask(
                epic,
                "Subtask1",
                "Subtask description",
                TaskStatus.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
        );

        manager.createSubtasks(subTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int expectedStatusCode = 200;
        String expectedBody = "{\"epicId\":1,\"id\":2,\"taskName\":\"Subtask1\",\"description\":\"Subtask description\"," +
                "\"taskStatus\":\"NEW\",\"duration\":5,\"startTime\":\"2025-02-28T09:00:00\",\"endTime\":\"2025-02-28T09:05:00\"}";
        int actuallyStatusCode = response.statusCode();
        String actuallyBody = response.body();

        assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 200");
        assertEquals(expectedBody, actuallyBody, "Ответ не совпадает с ожидаемым");
    }

    @Test
    public void testDeleteSubTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic(
                "Epic1",
                "Epic1 description",
                Duration.ofMinutes(0),
                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
        );

        manager.createEpic(epic);

        Subtask subTask = new Subtask(
                epic,
                "Subtask1",
                "Subtask description",
                TaskStatus.NEW,
                Duration.ofMinutes(5),
                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
        );

        manager.createSubtasks(subTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int expectedStatusCode = 200;
        int expectedSubTasksSize = 0;
        int actuallyStatusCode = response.statusCode();
        int actuallySubTasksSize = manager.getAllSubtasks().size();

        assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 200");
        assertEquals(expectedSubTasksSize, actuallySubTasksSize, "Менеджере задач не пуст");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", TaskStatus.NEW);
        manager.createTask(task);

        Epic epic = new Epic("Test Epic", "Epic Description");
        manager.createEpic(epic);

        Subtask subtask = new Subtask(epic.getId(),
                "Test Subtask",
                "Subtask Description",
                TaskStatus.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.now());
        manager.createSubtasks(subtask);
        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + HOSTNAME + ":" + PORT + "/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус код");

        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertNotNull(history, "История не должна быть null");
        assertEquals(3, history.size(), "Неверное количество задач в истории");
        assertEquals(subtask.getId(), history.get(0).getId(), "Подзадача должна быть первой в истории");
        assertEquals(epic.getId(), history.get(1).getId(), "Эпик должен быть вторым в истории");
        assertEquals(task.getId(), history.get(2).getId(), "Задача должна быть третьей в истории");
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task earlyTask = new Task("Early", "Desc", TaskStatus.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2023, 1, 1, 9, 0)); // 09:00

        Task lateTask = new Task("Late", "Desc", TaskStatus.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2023, 1, 1, 11, 0)); // 11:00

        manager.createTask(earlyTask);
        manager.createTask(lateTask);

        Epic epic = new Epic("Epic", "Desc");
        manager.createEpic(epic);

        Subtask earliestSubtask = new Subtask(epic.getId(), "Earliest", "Desc",
                TaskStatus.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(2023, 1, 1, 8, 0)); // 08:00
        manager.createSubtasks(earliestSubtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + HOSTNAME + ":" + PORT + "/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Set<Task> prioritized = gson.fromJson(response.body(), new TypeToken<Set<Task>>() {
        }.getType());

        assertEquals(3, prioritized.size(), "Должны быть 3 задачи (2 задачи + 1 подзадача)");
        List<Task> orderedTasks = new ArrayList<>(prioritized);
        assertEquals(earliestSubtask.getId(), orderedTasks.get(0).getId(), "Подзадача должна быть первой");
        assertEquals(earlyTask.getId(), orderedTasks.get(1).getId(), "Ранняя задача должна быть второй");
        assertEquals(lateTask.getId(), orderedTasks.get(2).getId(), "Поздняя задача должна быть третьей");
        assertTrue(prioritized.stream().anyMatch(t -> t.getId() == earliestSubtask.getId()));
        assertTrue(prioritized.stream().anyMatch(t -> t.getId() == earlyTask.getId()));
        assertTrue(prioritized.stream().anyMatch(t -> t.getId() == lateTask.getId()));
        assertFalse(prioritized.stream().anyMatch(t -> t.getId() == epic.getId()),
                "Эпик без времени не должен включаться");
    }

}