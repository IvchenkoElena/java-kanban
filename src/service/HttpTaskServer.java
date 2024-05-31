package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.List;


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

class BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
    }

    protected void writeResponse(HttpExchange h, String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        int code = 200;
        writeResponse(h, text, code);
    }

    protected void sendOk(HttpExchange h, String text) throws IOException {
        int code = 201;
        writeResponse(h, text, code);
    }

    protected void sendBadRequest(HttpExchange h, String text) throws IOException {
        int code = 400;
        writeResponse(h, text, code);
    }

    protected void sendNotFound(HttpExchange h,  String text) throws IOException {
        int code = 404;
        writeResponse(h, text, code);
    }
    protected void sendHasIntersections(HttpExchange h, String text) throws IOException {
        int code = 406;
        writeResponse(h, text, code);
    }

    protected void send500Exception(HttpExchange h) throws IOException {
        String text = "Что-то пошло не так";
        int code = 500;
        writeResponse(h, text, code);
    }
}

class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        if(localDateTime == null){
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(localDateTime.format(dtf));
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        if (jsonReader == null) {
            return null;
        }
        return LocalDateTime.parse(jsonReader.nextString(), dtf);
    }
}


class DurationAdapter extends TypeAdapter<Duration> { //вариант в минутах

    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if(duration == null){
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(Long.valueOf(duration.toMinutes()).toString());
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        if (jsonReader == null) {
            return null;
        }
        return Duration.ofMinutes(Long.parseLong(jsonReader.nextString()));
    }
}


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

        switch(method) {
            case "GET":
                if (pathParts.length == 3) {
                    try {
                        id = Integer.parseInt(path.split("/")[2]);
                        response = gson.toJson(taskManager.getTaskById(id));
                        sendText(httpExchange, response);
                    } catch(NumberFormatException e) {
                        sendBadRequest(httpExchange, "Введен некорректный ID");
                    } catch(NotFoundException e) {
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
                    } catch(IntersectionException e) {
                        sendHasIntersections(httpExchange, e.getMessage());
                    }
                } else {
                    try {
                        taskManager.updateTask(userTask);
                        sendOk(httpExchange,"Задача с Id " + id + " успешно обновлена");
                    } catch (NotFoundException e) {
                        sendNotFound(httpExchange, e.getMessage());
                    } catch(IntersectionException e) {
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
                    } catch(NumberFormatException e) {
                        sendBadRequest(httpExchange, "Введен некорректный ID");
                    } catch(NotFoundException e) {
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

        switch(method) {
            case "GET":
                if (pathParts.length == 3) {
                    try {
                        id = Integer.parseInt(path.split("/")[2]);
                        response = gson.toJson(taskManager.getSubtaskById(id));
                        sendText(httpExchange, response);
                    } catch(NumberFormatException e) {
                        sendBadRequest(httpExchange, "Введен некорректный ID");
                    } catch(NotFoundException e) {
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
                    } catch(IntersectionException e) {
                        sendHasIntersections(httpExchange, e.getMessage());
                    }
                } else {
                    try {
                        taskManager.updateSubtask(userSubtask);
                        sendOk(httpExchange,"Подзадача с Id " + id + " успешно обновлена");
                    } catch (NotFoundException e) {
                        sendNotFound(httpExchange, e.getMessage());
                    } catch(IntersectionException e) {
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
                    } catch(NumberFormatException e) {
                        sendBadRequest(httpExchange, "Введен некорректный ID");
                    } catch(NotFoundException e) {
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

        switch(method) {
            case "GET":
                if (pathParts.length == 3) {
                    try {
                        id = Integer.parseInt(path.split("/")[2]);
                        response = gson.toJson(taskManager.getEpicById(id));
                        sendText(httpExchange, response);
                    } catch(NumberFormatException e) {
                        sendBadRequest(httpExchange, "Введен некорректный ID");
                    } catch(NotFoundException e) {
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
                    } catch(IntersectionException e) {
                        sendHasIntersections(httpExchange, e.getMessage());
                    }
                } else {
                    try {
                        taskManager.updateEpic(userEpic);
                        sendOk(httpExchange,"Эпик с Id " + id + " успешно обновлен");
                    } catch (NotFoundException e) {
                        sendNotFound(httpExchange, e.getMessage());
                    } catch(IntersectionException e) {
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
                    } catch(NumberFormatException e) {
                        sendBadRequest(httpExchange, "Введен некорректный ID");
                    } catch(NotFoundException e) {
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

class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        System.out.println("Началась обработка " + method + " /history запроса от клиента.");
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        String response;
        if (method.equals("GET")) {
            List<Task> history = taskManager.getHistoryManager().getHistory();
            response = gson.toJson(history);
            sendText(httpExchange, response);
        } else {
            response = "Вы использовали какой-то другой метод!";
            sendBadRequest(httpExchange, response);
        }
    }
}

class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        System.out.println("Началась обработка " + method + " /prioritized запроса от клиента.");
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
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

