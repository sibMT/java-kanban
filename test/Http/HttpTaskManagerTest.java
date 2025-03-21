package Http;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import classes.Epic;
import classes.Subtask;
import classes.Task;
import classes.TaskStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import controllers.HistoryManager;
import controllers.InMemoryHistoryManager;
import controllers.InMemoryTaskManager;
import controllers.TaskManager;
import org.junit.jupiter.api.BeforeAll;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest {

    private static final String HOSTNAME = "localhost";
    private static final int PORT = 8080;
    private static HttpTaskServer taskServer;
    private static TaskManager manager;
    private static Gson gson;

    @BeforeAll
    public static void setUp() throws IOException {
        HistoryManager historyManager = new InMemoryHistoryManager(); // Создаем HistoryManager
        manager = new InMemoryTaskManager(historyManager); // Передаем его в конструктор
        taskServer = new HttpTaskServer(PORT, HOSTNAME, manager);
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        taskServer.start();
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

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json") // Добавляем заголовок
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный статус ответа");
        Task createdTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(createdTask, "Сервер не вернул созданную задачу");

        ArrayList<Task> tasksFromManager = manager.getAllTasks(); // Используем getAllTasks()
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getTaskName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddTaskInvalidData() throws IOException, InterruptedException {
        String invalidTaskJson = "{ \"name\": \"Invalid Task\" }";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(invalidTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Неверный статус ответа");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Testing task", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус ответа");

        List<Task> tasks = manager.getAllTasks();
        assertEquals(0, tasks.size(), "Задача не удалена");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Testing epic");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный статус ответа");

        List<Epic> epics = manager.getAllEpics();
        assertEquals(1, epics.size(), "Эпик не добавлен");
        assertEquals("Test Epic", epics.get(0).getTaskName(), "Некорректное имя эпика");
    }

    @Test
    public void testGetEpicNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Неверный статус ответа");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Testing epic");
        manager.createEpic(epic);

        Subtask subTask = new Subtask(epic.getId(), "Test Subtask", "Testing subtask", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String subTaskJson = gson.toJson(subTask);

        System.out.println("Отправляемый JSON: " + subTaskJson);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        System.out.println("Статус ответа: " + response.statusCode());
        System.out.println("Тело ответа: " + response.body());

        assertEquals(201, response.statusCode(), "Неверный статус ответа");
        Subtask createdSubtask = gson.fromJson(response.body(), Subtask.class);
        assertNotNull(createdSubtask, "Сервер не вернул созданную подзадачу");

        List<Subtask> subTasks = manager.getAllSubtasks();
        assertEquals(1, subTasks.size(), "Подзадача не добавлена");
        assertEquals("Test Subtask", subTasks.get(0).getTaskName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Testing task", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.createTask(task);
        manager.getTaskById(task.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус ответа");

        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());
        assertEquals(1, history.size(), "История неверна");
        assertEquals(task.getId(), history.get(0).getId(), "Некорректная задача в истории");
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Testing task 1", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(10));
        manager.createTask(task1);
        manager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус ответа");

        List<Task> prioritizedTasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());
        assertEquals(2, prioritizedTasks.size(), "Неверное количество задач");
        assertEquals(task1.getId(), prioritizedTasks.get(0).getId(), "Некорректная приоритетная задача");
    }
}