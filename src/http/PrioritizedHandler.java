package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.List;

import static http.HttpTaskServer.getGson;

class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        System.out.println("Началась обработка " + method + " /prioritized запроса от клиента.");
        Gson gson = getGson();
        String response;
        if (method.equals("GET")) {
            List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            response = gson.toJson(prioritizedTasks);
            sendText(httpExchange, response);
        } else {
            response = "Вы использовали какой-то другой метод!";
            sendBadRequest(httpExchange, response);
        }
    }
}