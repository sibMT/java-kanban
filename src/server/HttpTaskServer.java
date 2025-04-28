package server;

import com.sun.net.httpserver.HttpServer;
import controllers.Managers;
import controllers.TaskManager;
import server.handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {
    private final int port;
    private final String hostname;
    private final HttpServer httpServer;
    private static final int PORT = 8080;
    private static final String HOST = "localhost";


    public HttpTaskServer(int port, String hostname, TaskManager taskManager) throws IOException {
        this.port = port;
        this.hostname = hostname;

        InetSocketAddress address = new InetSocketAddress(HOST, PORT);
        httpServer = HttpServer.create(address, 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public void start() {
        System.out.printf("Сервер доступен по адресу http://%s:%d\n", hostname, port);
        httpServer.start();
        System.out.println("Сервер успешно запущен.");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop(int delay) {
        httpServer.stop(delay);
        System.out.println("Сервер остановлен");
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(PORT, HOST, Managers.getDefault());
        httpTaskServer.start();
    }
}
