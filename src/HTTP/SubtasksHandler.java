package HTTP;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import model.Subtask;
import service.IntersectionException;
import service.NotFoundException;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        System.out.println("Началась обработка " + method + " /subtasks запроса от клиента.");
        String path = httpExchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        String response;
        int id;

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        switch (method) {
            case "GET":
                if (pathParts.length == 3) {
                    try {
                        id = Integer.parseInt(path.split("/")[2]);
                        response = gson.toJson(taskManager.getSubtaskById(id));
                        sendText(httpExchange, response);
                    } catch (NumberFormatException e) {
                        sendBadRequest(httpExchange, "Введен некорректный ID");
                    } catch (NotFoundException e) {
                        sendNotFound(httpExchange, e.getMessage());
                    } catch (Exception e) {
                        send500Exception(httpExchange);
                    }
                } else {
                    try {
                        List<Subtask> subtasksList = taskManager.getAllSubtasksList();
                        response = gson.toJson(subtasksList);
                        sendText(httpExchange, response);
                    } catch (Exception e) {
                        send500Exception(httpExchange);
                    }
                }
                break;
            case "POST":
                Subtask userSubtask;
                String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    userSubtask = gson.fromJson(body, Subtask.class);
                } catch (JsonSyntaxException e) {
                    sendNotFound(httpExchange, "Некорректный ввод данных для создания Subtask");
                    break;
                }
                id = userSubtask.getId();
                if (id == 0) {
                    try {
                        int givenId = taskManager.createSubtask(userSubtask);
                        sendOk(httpExchange, "Подзадача с Id " + givenId + " успешно создана");
                    } catch (IntersectionException e) {
                        sendHasIntersections(httpExchange, e.getMessage());
                    }
                } else {
                    try {
                        taskManager.updateSubtask(userSubtask);
                        sendOk(httpExchange, "Подзадача с Id " + id + " успешно обновлена");
                    } catch (NotFoundException e) {
                        sendNotFound(httpExchange, e.getMessage());
                    } catch (IntersectionException e) {
                        sendHasIntersections(httpExchange, e.getMessage());
                    }
                }
                break;
            case "DELETE":
                if (pathParts.length == 3) {
                    try {
                        id = Integer.parseInt(path.split("/")[2]);
                        taskManager.deleteSubtaskById(id);
                        response = "Подзадача успешно удалена";
                        sendOk(httpExchange, response);
                    } catch (NumberFormatException e) {
                        sendBadRequest(httpExchange, "Введен некорректный ID");
                    } catch (NotFoundException e) {
                        sendNotFound(httpExchange, "Подзадача с id " + path.split("/")[2] + " не найдена");
                    }
                } else {
                    send500Exception(httpExchange);
                }
                break;
            default:
                sendBadRequest(httpExchange, "Вы использовали какой-то другой метод!");
        }
    }
}