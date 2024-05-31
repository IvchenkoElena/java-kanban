package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import service.IntersectionException;
import service.NotFoundException;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        System.out.println("Началась обработка " + method + " /epic запроса от клиента.");
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
                        response = gson.toJson(taskManager.getEpicById(id));
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
                        List<Epic> epicsList = taskManager.getAllEpicsList();
                        response = gson.toJson(epicsList);
                        sendText(httpExchange, response);
                    } catch (Exception e) {
                        send500Exception(httpExchange);
                    }
                }
                break;
            case "POST":
                Epic userEpic;
                String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    userEpic = gson.fromJson(body, Epic.class);
                } catch (JsonSyntaxException e) {
                    sendNotFound(httpExchange, "Некорректный ввод данных для создания Epic");
                    break;
                }
                id = userEpic.getId();
                if (id == 0) {
                    try {
                        int givenId = taskManager.createEpic(userEpic);
                        sendOk(httpExchange, "Эпик с Id " + givenId + " успешно создан");
                    } catch (IntersectionException e) {
                        sendHasIntersections(httpExchange, e.getMessage());
                    }
                } else {
                    try {
                        taskManager.updateEpic(userEpic);
                        sendOk(httpExchange, "Эпик с Id " + id + " успешно обновлен");
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
                        taskManager.deleteEpicById(id);
                        response = "Эпик успешно удален";
                        sendOk(httpExchange, response);
                    } catch (NumberFormatException e) {
                        sendBadRequest(httpExchange, "Введен некорректный ID");
                    } catch (NotFoundException e) {
                        sendNotFound(httpExchange, "Эпик с id " + path.split("/")[2] + " не найден");
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