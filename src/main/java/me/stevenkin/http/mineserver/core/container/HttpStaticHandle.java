package me.stevenkin.http.mineserver.core.container;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.stevenkin.http.mineserver.core.entry.HttpRequest;
import me.stevenkin.http.mineserver.core.entry.HttpResponse;
import me.stevenkin.http.mineserver.core.exception.NoFoundException;
import me.stevenkin.http.mineserver.core.util.ConfigUtil;
import me.stevenkin.http.mineserver.core.util.FileUtil;

/**
 * Created by wjg on 16-4-26.
 */
public class HttpStaticHandle extends AbstractHandle {
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

    private String basePath = ConfigUtil.getConfig("basePath","/home/wjg/server");

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String path = ((List<String>)request.getAttribute("matcherStrList")).get(0);
        String file;
        int index = path.indexOf("?");
        file = path.substring(0,index<0?path.length():index);
        String filePath = basePath+file;
        File f = new File(filePath);
        if(!f.exists()){
            throw new NoFoundException();
        }
        byte[] body = FileUtil.getFileContent(f);
        String mime = FileUtil.getFileMimeType(f);
        response.addHeader(CONTENT_TYPE, mime);
        Date lastModified = new Date(f.lastModified());
        response.addHeader(LAST_MODIFIED,
                formater.format(lastModified));
        response.addHeader(CONNECTION, KEEP_ALIVE);
        response.getOutput().write(body);
    }
}
