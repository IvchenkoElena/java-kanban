package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

abstract class BaseHttpHandler implements HttpHandler {

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

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        int code = 404;
        writeResponse(h, text, code);
    }

    protected void sendMethodNotAllowed(HttpExchange h) throws IOException {
        String text = "Вы использовали некорректный метод";
        int code = 405;
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