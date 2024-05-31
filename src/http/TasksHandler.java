package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.IntersectionException;
import service.NotFoundException;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class TasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        System.out.println("Началась обработка " + method + " /tasks запроса от клиента.");
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
                        response = gson.toJson(taskManager.getTaskById(id));
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
                        List<Task> tasksList = taskManager.getAllTasksList();
                        response = gson.toJson(tasksList);
                        sendText(httpExchange, response);
                    } catch (Exception e) {
                        send500Exception(httpExchange);
                    }
                }
                break;
            case "POST":
                Task userTask;
                String body = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    userTask = gson.fromJson(body, Task.class);
                } catch (JsonSyntaxException e) {
                    sendNotFound(httpExchange, "Некорректный ввод данных для создания Task");
                    //sendNotFound(httpExchange, "Некорректный ввод данных для создания Task" + e.getMessage()); //хотела еще добавить сообщение от exception,
                    // но тогда в предыдущей строке удаляются пробелы, не нашла как лечить
                    break;
                }
                id = userTask.getId();
                if (id == 0) {
                    try {
                        int givenId = taskManager.createTask(userTask);
                        sendOk(httpExchange, "Задача с Id " + givenId + " успешно создана");
                    } catch (IntersectionException e) {
                        sendHasIntersections(httpExchange, e.getMessage());
                    }
                } else {
                    try {
                        taskManager.updateTask(userTask);
                        sendOk(httpExchange, "Задача с Id " + id + " успешно обновлена");
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
                        taskManager.deleteTaskById(id);
                        response = "Задача успешно удалена";
                        sendOk(httpExchange, response);
                    } catch (NumberFormatException e) {
                        sendBadRequest(httpExchange, "Введен некорректный ID");
                    } catch (NotFoundException e) {
                        sendNotFound(httpExchange, "Задача с id " + path.split("/")[2] + " не найдена");
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