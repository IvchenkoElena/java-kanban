package http;

import com.sun.net.httpserver.HttpServer;
import service.Managers;
import service.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;

public class HttpTaskServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create();

        File file = Path.of("src/database/file2.csv").toFile();
        TaskManager taskManager = Managers.getDefault(file);
        //TaskManager taskManager = FileBackedTaskManager.loadFromFile(file); // этот вариант для проверки из готового файла
        // заменить обратно, когда будут готовы все хэндлеры

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }
}