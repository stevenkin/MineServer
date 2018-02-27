package me.stevenkin.http.mineserver.core.container.bean;

import me.stevenkin.http.mineserver.core.container.HttpSessionManager;
import me.stevenkin.http.mineserver.core.entry.HttpRequest;
import me.stevenkin.http.mineserver.core.entry.HttpResponse;

/**
 * Created by wjg on 16-4-27.
 */
public class HttpContext {
    private HttpRequest request;
    private HttpResponse response;
    private HttpSessionManager sessionManager;

    public HttpContext(HttpRequest request, HttpResponse response,HttpSessionManager sessionManager) {
        this.request = request;
        this.response = response;
        this.sessionManager = sessionManager;
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
