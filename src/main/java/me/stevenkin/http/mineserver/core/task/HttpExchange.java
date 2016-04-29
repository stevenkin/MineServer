package me.stevenkin.http.mineserver.core.task;

import me.stevenkin.http.mineserver.core.container.HttpContainer;
import me.stevenkin.http.mineserver.core.entry.HttpRequest;
import me.stevenkin.http.mineserver.core.entry.HttpResponse;
import me.stevenkin.http.mineserver.core.exception.NoFoundException;
import me.stevenkin.http.mineserver.core.util.ErrorMessageUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wjg on 16-4-23.
 */
public class HttpExchange implements Runnable {
    private static final DateFormat formater = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

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
        try {
            container.doProcess(request,response);
            response.setCode("200");
            response.setMessage("OK");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if(e instanceof NoFoundException) {
                    response.setCode("404");
                    response.setMessage("Not Found");
                    response.getOutput().write(ErrorMessageUtil.ERROR_404.getBytes(Charset.forName("ISO-8859-1")));
                }
                else {
                    response.setCode("500");
                    response.setMessage("Internal Server Error");
                    response.getOutput().write(ErrorMessageUtil.ERROR_500.getBytes(Charset.forName("ISO-8859-1")));
                }
            } catch (IOException e1) {
            }
        }
        response.addHeader("Date",formater.format(new Date()));
        byte[] bodyBytes = response.getOutput().toByteArray();
        response.addHeader("Content-Length",Integer.toString(bodyBytes.length));
        byte[] headerBytes = response.headersToBytes();
        ByteBuffer responseBuffer = ByteBuffer.allocate(headerBytes.length+bodyBytes.length);
        responseBuffer.put(headerBytes).put(bodyBytes);
        responseBuffer.flip();
        key.attach(responseBuffer);
        key.interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
    }
}
