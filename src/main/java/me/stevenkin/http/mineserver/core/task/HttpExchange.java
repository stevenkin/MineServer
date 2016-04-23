package me.stevenkin.http.mineserver.core.task;

import me.stevenkin.http.mineserver.core.entry.HttpRequest;
import me.stevenkin.http.mineserver.core.entry.HttpResponse;

import java.nio.channels.SelectionKey;

/**
 * Created by wjg on 16-4-23.
 */
public class HttpExchange {
    private HttpRequest request;
    private HttpResponse response;
    private SelectionKey key;

    public HttpExchange() {
    }

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    public SelectionKey getKey() {
        return key;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }
}
