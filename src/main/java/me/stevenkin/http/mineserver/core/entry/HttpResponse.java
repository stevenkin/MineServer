package me.stevenkin.http.mineserver.core.entry;

import me.stevenkin.http.mineserver.core.util.FileUtil;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wjg on 16-4-15.
 */
public class HttpResponse {
    private static final DateFormat formater = new SimpleDateFormat(
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
            response.addHeader(CONTENT_LENGTH, response.body.length);
            Date lastModified = new Date(file.lastModified());
            response.addHeader(LAST_MODIFIED,
                    formater.format(lastModified));
        }else{
            response.status(NOT_FOUND_404);
            response.body = new byte[0];
        }
        response.addHeader(CONNECTION, KEEP_ALIVE);

        return response;
    }

    public static HttpResponse buildError500Response(){
        HttpResponse response = new HttpResponse();
        response.status(SERVER_ERROR_500).body = new byte[0];
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
    }
}
