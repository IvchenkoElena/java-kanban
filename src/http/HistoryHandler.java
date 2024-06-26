package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.List;

import static http.HttpTaskServer.getGson;

class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        System.out.println("Началась обработка " + method + " /history запроса от клиента.");
        Gson gson = getGson();
        String response;
        if (method.equals("GET")) {
            List<Task> history = taskManager.getHistoryList();
            response = gson.toJson(history);
            sendText(httpExchange, response);
        } else {
            sendMethodNotAllowed(httpExchange);
        }
    }
}