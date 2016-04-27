package me.stevenkin.http.mineserver.core.entry;

import me.stevenkin.http.mineserver.core.util.FileUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wjg on 16-4-15.
 */
public class HttpResponse {

    private String code;
    private String message;
    private String protocol;

    private List<Header> headers = new LinkedList<>();

    private ByteArrayOutputStream output = new ByteArrayOutputStream();

    private HttpRequest request;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void addHeader(String name,String value){
        headers.add(new Header(name,value));
    }

    public ByteArrayOutputStream getOutput() {
        return output;
    }

    public BufferedWriter getWrite(char[] chars){
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.getOutput()));
        return writer;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
        this.protocol = "HTTP/1.1";
        this.request = request;
    }

    public byte[] headersToBytes(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.protocol).append(" ").append(this.code).append(this.message).append("\r\n");
        for(Header header:this.headers){
            stringBuilder.append(header.getName()).append(": ").append(header.getValue()).append("\r\n");
        }
        stringBuilder.append("\r\n");
        return stringBuilder.toString().getBytes(Charset.forName("ISO-8859-1"));
    }

    public void addCookie(Cookie cookie){

    }

    /*private static final DateFormat formater = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

    public static final String OK_200 = "HTTP/1.1 200 OK";
    public static final String NEWLINE = "\r\n";
    public static final String NOT_FOUND_404 = "HTTP/1.1 404 Not Find";
    public static final String SERVER_ERROR_500 = "HTTP/1.1 500 Internal Server Error";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONNECTION = "Connection";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String KEEP_ALIVE = "keep-alive";
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    public static final String LAST_MODIFIED = "Last-Modified";
    public static final String GZIP = "gzip";

    private String status;
    private Map<String, Object> header = new TreeMap<String, Object>();

    private byte[] body;

    public HttpResponse addHeader(String key, Object value) {
        header.put(key, value);
        return this;
    }

    public HttpResponse status(String status){
        this.status = status;
        return this;
    }

    public byte[] getHeader() {
        return toString().getBytes();
    }

    public byte[] getBody(){
        return this.body;
    }

    public static HttpResponse buildResponse(String root,String path) throws Exception {
        HttpResponse response = new HttpResponse();
        String filePath = (root.endsWith("/")?root.substring(0,root.length()-1):root)+path;
        File file = new File(filePath);
        if(file.exists()){
            response.status(OK_200);
            response.addHeader(CONTENT_TYPE, FileUtil.getFileMimeType(file));

            // response body byte, exception throws here
            response.body = FileUtil.getFileContent(file);
            Date lastModified = new Date(file.lastModified());
            response.addHeader(LAST_MODIFIED,
                    formater.format(lastModified));
        }else{
            response.status(NOT_FOUND_404);
            response.body = "http 404 not found".getBytes();

        }
        response.addHeader(CONTENT_LENGTH, response.body.length);
        response.addHeader(CONNECTION, KEEP_ALIVE);

        return response;
    }

    public static HttpResponse buildError500Response(){
        HttpResponse response = new HttpResponse();
        response.status(SERVER_ERROR_500).addHeader(CONTENT_LENGTH, response.body.length).body = "http 500 error".getBytes();
        return response;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(120);
        sb.append(status).append(NEWLINE);
        Set<String> keySet = header.keySet();
        for (String key : keySet) {
            sb.append(key).append(": ").append(header.get(key)).append(NEWLINE);
        }
        sb.append(NEWLINE); // empty line;
        return sb.toString();
    }*/
}
