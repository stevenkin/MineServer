package me.stevenkin.http.mineserver.core.entry;

import me.stevenkin.http.mineserver.core.processor.HttpParser;

import java.net.HttpCookie;
import java.util.*;

/**
 * Created by wjg on 16-4-15.
 */
public class HttpRequest {
    private HttpParser.METHOD method;
    private String path;
    private String protocol;

    private String appcept;
    private String acceptCharset;
    private String acceptEncoding;
    private String acceptLanguage;
    private String connection;
    private List<Cookie> cookies = new LinkedList<Cookie>();
    private long contentLength;
    private String contentType;
    private Date date;
    private String host;

    private List<Header> headers = new LinkedList<Header>();
    private byte[] body = new byte[0];
    private Map<String,String> params = new HashMap<String,String>();

    public HttpRequest(HttpParser.METHOD method, String path, String protocol) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
    }

    public void setMethod(String method) {
        this.method = HttpParser.METHOD.methodOf(method);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAppcept() {
        return appcept;
    }

    public void setAppcept(String appcept) {
        this.appcept = appcept;
    }

    public String getAcceptCharset() {
        return acceptCharset;
    }

    public void setAcceptCharset(String acceptCharset) {
        this.acceptCharset = acceptCharset;
    }

    public String getAcceptEncoding() {
        return acceptEncoding;
    }

    public void setAcceptEncoding(String acceptEncoding) {
        this.acceptEncoding = acceptEncoding;
    }

    public String getAcceptLanguage() {
        return acceptLanguage;
    }

    public void setAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void addCookies(Cookie cookie) {
        this.cookies.add(cookie);
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(Header header) {
        this.headers.add(header);
    }

    public HttpParser.METHOD getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParam(String name,String value){
        this.params.put(name,value);
    }
}
