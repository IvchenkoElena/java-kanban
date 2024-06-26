package http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import service.IntersectionException;
import service.NotFoundException;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static http.HttpTaskServer.getGson;

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

        Gson gson = getGson();

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
                } else if (pathParts.length == 2) {
                    try {
                        List<Epic> epicsList = taskManager.getAllEpicsList();
                        response = gson.toJson(epicsList);
                        sendText(httpExchange, response);
                    } catch (Exception e) {
                        send500Exception(httpExchange);
                    }
                } else {
                    sendBadRequest(httpExchange, "Передан некорректный url запроса");
                }
                break;
            case "POST":
                Epic userEpic;
                String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    userEpic = gson.fromJson(body, Epic.class);
                } catch (JsonSyntaxException e) {
                    sendNotFound(httpExchange, "Некорректный ввод данных в теле запроса для создания Epic");
                    break;
                }
                id = userEpic.getId();
                if (id == 0) {
                    try {
                        int givenId = taskManager.createEpic(userEpic);
                        sendOk(httpExchange, "Эпик с Id " + givenId + " успешно создан");
                    } catch (IntersectionException e) {
                        sendHasIntersections(httpExchange, e.getMessage());
                    } catch (Exception e) {
                        send500Exception(httpExchange);
                    }
                } else {
                    try {
                        taskManager.updateEpic(userEpic);
                        sendOk(httpExchange, "Эпик с Id " + id + " успешно обновлен");
                    } catch (NotFoundException e) {
                        sendNotFound(httpExchange, e.getMessage());
                    } catch (IntersectionException e) {
                        sendHasIntersections(httpExchange, e.getMessage());
                    } catch (Exception e) {
                        send500Exception(httpExchange);
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
                    } catch (Exception e) {
                        send500Exception(httpExchange);
                    }
                } else if (pathParts.length == 2) {
                    try {
                        taskManager.deleteAllEpics();
                        sendOk(httpExchange, "Все эпики удалены");
                    } catch (Exception e) {
                        send500Exception(httpExchange);
                    }
                } else {
                    sendBadRequest(httpExchange, "Передан некорректный url запроса");
                }
                break;
            default:
                sendMethodNotAllowed(httpExchange);
        }
    }
}