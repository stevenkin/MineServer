package me.stevenkin.http.mineserver.core.entry;

import me.stevenkin.http.mineserver.core.parser.HttpRequestParser;

/**
 * Created by wjg on 16-4-15.
 */
public class HttpRequest {
    private HttpRequestParser.METHOD method;
    private String path;
    private String protocol;

    public HttpRequest() {
    }

    public HttpRequest(HttpRequestParser.METHOD method, String path, String protocol) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
    }

    public HttpRequestParser.METHOD getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }
}
