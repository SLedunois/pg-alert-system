package fr.sledunois.pgas.utils;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

public class HttpUtils {

    private HttpUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static void renderJson(HttpServerResponse response, int code, String value) {
        response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                .putHeader(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.NO_CACHE)
                .putHeader(HttpHeaderNames.EXPIRES, "-1").setStatusCode(code).end(value);
    }

    public static void ok(HttpServerResponse response, JsonObject object) {
        renderJson(response, HttpResponseStatus.OK.code(), object.encode());
    }

    public static void created(HttpServerResponse response, JsonObject object) {
        renderJson(response, HttpResponseStatus.CREATED.code(), object.encode());
    }

    public static void noContent(HttpServerResponse response) {
        response.putHeader(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.NO_CACHE)
                .putHeader(HttpHeaderNames.EXPIRES, "-1").setStatusCode(HttpResponseStatus.NO_CONTENT.code()).end();
    }

    public static void internalServerError(HttpServerResponse response, String error) {
        JsonObject err = new JsonObject().put("error", error);
        renderJson(response, HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), err.encode());
    }
}
