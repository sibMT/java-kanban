package Http;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import classes.Epic;
import classes.Subtask;
import classes.Task;
import classes.TaskStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controllers.HistoryManager;
import controllers.InMemoryHistoryManager;
import controllers.InMemoryTaskManager;
import controllers.TaskManager;
//import dto.EpicDTO;
//import dto.SubtaskDTO;
//import dto.TaskDTO;
import org.junit.jupiter.api.BeforeAll;
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
//import java.util.List;

import org.junit.jupiter.api.*;
//import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest {

    private static final String HOSTNAME = "localhost";
    private static final int PORT = 8081;
    private static HttpTaskServer taskServer;
    private static TaskManager manager;
    private static Gson gson;
    private final HttpClient client = HttpClient.newHttpClient();

    @BeforeAll
    public static void setUp() throws IOException {
        HistoryManager historyManager = new InMemoryHistoryManager();
        manager = new InMemoryTaskManager(historyManager);
        taskServer = new HttpTaskServer(PORT, HOSTNAME, manager);
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        taskServer.start();
        try {
            Thread.sleep(1000); // 1 секунда
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void tearDown() {
        taskServer.stop(0);
    }

    @BeforeEach
    public void resetManager() {
        manager.removeAllTasks();
        manager.removeAllEpics();
        manager.removeAllSubtasks();
    }

//    @Test
//    public void testPostTask() throws IOException, InterruptedException {
//        TaskDTO taskDto = new TaskDTO(
//                "Task1",
//                "Task description",
//                TaskStatus.NEW,
//                Duration.ofMinutes(5),
//                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
//        );
//        String json = gson.toJson(taskDto);
//        System.out.println("Отправляемый JSON: " + json);
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:" + PORT + "/tasks"))
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(json))
//                .build();
//
//        System.out.println("Отправка запроса: " + request.uri());
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println("Получен ответ: " + response.statusCode() + " " + response.body());
//
//        assertEquals(201, response.statusCode(), "Статус код должен быть 201");
//        List<Task> tasks = manager.getAllTasks();
//        assertNotNull(tasks, "Список задач не должен быть null");
//        assertEquals(1, tasks.size(), "Некорректное количество задач");
//
//        Task createdTask = tasks.get(0);
//        assertEquals("Task1", createdTask.getTaskName(), "Некорректное имя задачи");
//    }
//
//    @Test
//    public void testUpdateTaskById() throws IOException, InterruptedException {
//        TaskDTO taskDto = new TaskDTO(
//                "Task1",
//                "Task description",
//                TaskStatus.NEW,
//                Duration.ofMinutes(5),
//                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
//        );
//
//        String json = gson.toJson(taskDto);
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8081/tasks"))
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(json))
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        taskDto = new TaskDTO(
//                "Task2",
//                "Task description",
//                TaskStatus.NEW,
//                Duration.ofMinutes(5),
//                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
//        );
//
//        json = gson.toJson(taskDto);
//
//        request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8081/tasks/1"))
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(json))
//                .build();
//
//        response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        int expectedStatusCode = 201;
//        int expectedTasksSize = 1;
//        String expectedTaskName = "Task2";
//        int actuallyStatusCode = response.statusCode();
//        int actuallyTasksSize = manager.getAllTasks().size();
//        String actuallyTaskName = manager.getAllTasks().getFirst().getTaskName();
//
//        Assertions.assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 201");
//        Assertions.assertEquals(expectedTasksSize, actuallyTasksSize, "Некорректное количество задач");
//        Assertions.assertEquals(expectedTaskName, actuallyTaskName, "Некорректное имя задачи");
//    }

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
                .uri(URI.create("http://localhost:8081/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int expectedStatusCode = 200;
        int actuallyStatusCode = response.statusCode();
        Assertions.assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 200");

        String expectedBody = "[{\"id\":1,\"taskName\":\"Task1\",\"description\":\"Task description\",\"taskStatus\":\"NEW\",\"duration\":\"PT5M\",\"startTime\":\"2025-02-28T09:00:00\",\"endTime\":\"2025-02-28T09:05:00\"}]";
        String actuallyBody = response.body();
        Assertions.assertEquals(expectedBody, actuallyBody, "Ответ не совпадает с ожидаемым");
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
                .uri(URI.create("http://localhost:8081/tasks/1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int expectedStatusCode = 200;
        int actuallyStatusCode = response.statusCode();
        Assertions.assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 200");

        String expectedBody = "{\"id\":1,\"taskName\":\"Task1\",\"description\":\"Task description\",\"taskStatus\":\"NEW\",\"duration\":\"PT5M\",\"startTime\":\"2025-02-28T09:00:00\",\"endTime\":\"2025-02-28T09:05:00\"}";
        String actuallyBody = response.body();
        Assertions.assertEquals(expectedBody, actuallyBody, "Ответ не совпадает с ожидаемым");
    }

//    @Test
//    public void testDeleteTaskById() throws IOException, InterruptedException {
//        TaskDTO taskDto = new TaskDTO(
//                "Task1",
//                "Task description",
//                TaskStatus.NEW,
//                Duration.ofMinutes(5),
//                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
//        );
//
//        String json = gson.toJson(taskDto);
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8081/tasks"))
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(json))
//                .build();
//
//        client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8081/tasks/1"))
//                .DELETE()
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        int expectedStatusCode = 200;
//        int expectedTasksSize = 0;
//        int actuallyStatusCode = response.statusCode();
//        int actuallyTasksSize = manager.getAllTasks().size();
//
//        Assertions.assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 200");
//        Assertions.assertEquals(expectedTasksSize, actuallyTasksSize, "Менеджере задач не пуст");
//    }

//    @Test
//    public void testPostEpic() throws IOException, InterruptedException {
//        EpicDTO epicDto = new EpicDTO("Epic1", "Epic1 description");
//
//        String json = gson.toJson(epicDto);
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8081/epics"))
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(json))
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        int expectedStatusCode = 201;
//        int expectedEpicsSize = 1;
//        String expectedEpicName = "Epic1";
//        int actuallyStatusCode = response.statusCode();
//        int actuallyEpicsSize = manager.getAllEpics().size();
//        String actuallyEpicsName = manager.getAllEpics().getFirst().getTaskName();
//
//        Assertions.assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 201");
//        Assertions.assertEquals(expectedEpicsSize, actuallyEpicsSize, "Некорректное количество эпиков");
//        Assertions.assertEquals(expectedEpicName, actuallyEpicsName, "Некорректное имя задачи");
//    }

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
                .uri(URI.create("http://localhost:8081/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int expectedStatusCode = 200;
        String expectedBody = "[{\"subtasks\":[],\"id\":1,\"taskName\":\"Epic1\",\"description\":\"Epic1 description\",\"taskStatus\":\"NEW\",\"duration\":\"PT0S\",\"startTime\":\"2025-02-28T09:00:00\",\"endTime\":\"2025-02-28T09:00:00\"}]";
        int actuallyStatusCode = response.statusCode();
        String actuallyBody = response.body();

        Assertions.assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 200");
        Assertions.assertEquals(expectedBody, actuallyBody, "Ответ не совпадает с ожидаемым");
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
                .uri(URI.create("http://localhost:8081/epics/1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int expectedStatusCode = 200;
        String expectedBody = "{\"subtasks\":[],\"id\":1,\"taskName\":\"Epic1\",\"description\":\"Epic1 description\",\"taskStatus\":\"NEW\",\"duration\":\"PT0S\",\"startTime\":\"2025-02-28T09:00:00\",\"endTime\":\"2025-02-28T09:00:00\"}";
        int actuallyStatusCode = response.statusCode();
        String actuallyBody = response.body();

        Assertions.assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 200");
        Assertions.assertEquals(expectedBody, actuallyBody, "Ответ не совпадает с ожидаемым");
    }

//    @Test
//    public void testGetEpicSubtasks() throws IOException, InterruptedException {
//        Epic epic = new Epic(
//                "Epic1",
//                "Epic1 description",
//                Duration.ofMinutes(0),
//                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
//        );
//
//        manager.createEpic(epic);
//
//        Subtask subtask = new Subtask(
//                epic,
//                "Subtask1",
//                "Subtask description",
//                TaskStatus.NEW,
//                Duration.ofMinutes(10),
//                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
//        );
//
//        manager.createSubtasks(subtask);
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8081/epics/1/subtasks"))
//                .GET()
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        int expectedStatusCode = 200;
//        String expectedBody = "[{\"epicId\":1,\"id\":2,\"taskName\":\"SubTask1\",\"description\":\"SubTask description\"" +
//                ",\"taskStatus\":\"NEW\",\"duration\":\"10\",\"startTime\":\"09:00:00/28.02.2025\",\"endTime\":\"09:10:00/28.02.2025\"}]";
//        int actuallyStatusCode = response.statusCode();
//        String actuallyBody = response.body();
//
//        Assertions.assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 200");
//        Assertions.assertEquals(expectedBody, actuallyBody, "Ответ не совпадает с ожидаемым");
//    }

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
                .uri(URI.create("http://localhost:8081/epics/1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int expectedStatusCode = 200;
        int expectedEpicsSize = 0;
        int actuallyStatusCode = response.statusCode();
        int actuallyEpicsSize = manager.getAllTasks().size();

        Assertions.assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 200");
        Assertions.assertEquals(expectedEpicsSize, actuallyEpicsSize, "Менеджере задач не пуст");
    }

//    @Test
//    public void testPostSubTask() throws IOException, InterruptedException {
//        Epic epic = new Epic(
//                "Epic1",
//                "Epic1 description",
//                Duration.ofMinutes(0),
//                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
//        );
//
//        manager.createEpic(epic);
//
//        SubtaskDTO subtaskDto = new SubtaskDTO(
//                1,
//                "Subtask1",
//                "Subtask1 description",
//                TaskStatus.NEW,
//                Duration.ofMinutes(5),
//                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
//        );
//
//        String json = gson.toJson(subtaskDto);
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8081/subtasks"))
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(json))
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        int expectedStatusCode = 201;
//        int expectedEpicsSize = 1;
//        String expectedSubtaskName = "Subtask1";
//        int actuallyStatusCode = response.statusCode();
//        int actuallySubtasksSize = manager.getAllSubtasks().size();
//        String actuallySubtaskName = manager.getAllSubtasks().getFirst().getTaskName();
//
//        Assertions.assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 201");
//        Assertions.assertEquals(expectedEpicsSize, actuallySubtasksSize, "Некорректное количество подзадач");
//        Assertions.assertEquals(expectedSubtaskName, actuallySubtaskName, "Некорректное имя задачи");
//    }
//
//    @Test
//    public void testUpdateSubTask() throws IOException, InterruptedException {
//        Epic epic = new Epic(
//                "Epic1",
//                "Epic1 description",
//                Duration.ofMinutes(0),
//                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
//        );
//        manager.createEpic(epic);
//        Subtask subtask = new Subtask(
//                epic,
//                "Subtask1",
//                "Subtask description",
//                TaskStatus.NEW,
//                Duration.ofMinutes(5),
//                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
//        );
//        manager.createSubtasks(subtask);
//        SubtaskDTO subTaskDto = new SubtaskDTO(
//                1,
//                "Subtask1 updated",
//                "Subtask1 description",
//                TaskStatus.NEW,
//                Duration.ofMinutes(5),
//                LocalDateTime.of(LocalDate.of(2025, 2, 28), LocalTime.of(9, 0))
//        );
//
//        String json = gson.toJson(subTaskDto);
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8081/subtasks/2"))
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(json))
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        int expectedStatusCode = 201;
//        int expectedSubTasksSize = 1;
//        String expectedSubtasksName = "Subtask1 updated";
//        int actuallyStatusCode = response.statusCode();
//        int actuallySubTasksSize = manager.getAllSubtasks().size();
//        String actuallySubtaskName = manager.getAllSubtasks().getFirst().getTaskName();
//
//        Assertions.assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 201");
//        Assertions.assertEquals(expectedSubTasksSize, actuallySubTasksSize, "Некорректное количество подзадач");
//        Assertions.assertEquals(expectedSubtasksName, actuallySubtaskName, "Некорректное имя подзадачи");
//    }

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
                .uri(URI.create("http://localhost:8081/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int expectedStatusCode = 200;
        String expectedBody = "[{\"epicId\":1,\"id\":2,\"taskName\":\"Subtask1\",\"description\":\"Subtask description\",\"taskStatus\":\"NEW\",\"duration\":\"PT5M\",\"startTime\":\"2025-02-28T09:00:00\",\"endTime\":\"2025-02-28T09:05:00\"}]";
        int actuallyStatusCode = response.statusCode();
        String actuallyBody = response.body();

        Assertions.assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 200");
        Assertions.assertEquals(expectedBody, actuallyBody, "Ответ не совпадает с ожидаемым");
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
                .uri(URI.create("http://localhost:8081/subtasks/2"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int expectedStatusCode = 200;
        String expectedBody = "{\"epicId\":1,\"id\":2,\"taskName\":\"Subtask1\",\"description\":\"Subtask description\"" +
                ",\"taskStatus\":\"NEW\",\"duration\":\"PT5M\",\"startTime\":\"2025-02-28T09:00:00\",\"endTime\":\"2025-02-28T09:05:00\"}";
        int actuallyStatusCode = response.statusCode();
        String actuallyBody = response.body();

        Assertions.assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 200");
        Assertions.assertEquals(expectedBody, actuallyBody, "Ответ не совпадает с ожидаемым");
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
                .uri(URI.create("http://localhost:8081/subtasks/2"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int expectedStatusCode = 200;
        int expectedSubTasksSize = 0;
        int actuallyStatusCode = response.statusCode();
        int actuallySubTasksSize = manager.getAllSubtasks().size();

        Assertions.assertEquals(expectedStatusCode, actuallyStatusCode, "Статус код должен быть 200");
        Assertions.assertEquals(expectedSubTasksSize, actuallySubTasksSize, "Менеджере задач не пуст");
    }
}