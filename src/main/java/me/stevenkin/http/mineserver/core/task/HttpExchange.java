package me.stevenkin.http.mineserver.core.task;

import me.stevenkin.http.mineserver.core.container.HttpContainer;
import me.stevenkin.http.mineserver.core.entry.HttpRequest;
import me.stevenkin.http.mineserver.core.entry.HttpResponse;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/**
 * Created by wjg on 16-4-23.
 */
public class HttpExchange implements Runnable {
    private HttpRequest request;
    private HttpResponse response;
    private SelectionKey key;

    private HttpContainer container;
    private Selector selector;

    public HttpExchange() {
    }

    public HttpExchange(HttpRequest request, HttpResponse response, SelectionKey key, HttpContainer container, Selector selector) {
        this.request = request;
        this.response = response;
        this.key = key;
        this.container = container;
        this.selector = selector;
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

    public HttpContainer getContainer() {
        return container;
    }

    public void setContainer(HttpContainer container) {
        this.container = container;
    }

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        byte[] headerBytes = null;
        byte[] bodyBytes = null;
        try {
            container.doProcess(request,response);
            headerBytes = response.headersToBytes();
            bodyBytes = response.getOutput().toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            headerBytes = null;//TODO
            bodyBytes = null;
        }
        ByteBuffer responseBuffer = ByteBuffer.allocate(headerBytes.length+bodyBytes.length);
        responseBuffer.put(headerBytes).put(bodyBytes);
        responseBuffer.flip();
        key.attach(responseBuffer);
        key.interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
    }
}
