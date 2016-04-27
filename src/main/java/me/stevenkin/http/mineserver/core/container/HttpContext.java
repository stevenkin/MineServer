package me.stevenkin.http.mineserver.core.container;

import me.stevenkin.http.mineserver.core.entry.HttpRequest;
import me.stevenkin.http.mineserver.core.entry.HttpResponse;

/**
 * Created by wjg on 16-4-27.
 */
public class HttpContext {
    private HttpRequest request;
    private HttpResponse response;
    private HttpSessionManager sessionManager = new HttpSessionManager();

    public HttpContext(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public HttpSessionManager getSessionManager() {
        return sessionManager;
    }
}
