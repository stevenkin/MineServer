package me.stevenkin.http.mineserver.core.container;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.stevenkin.http.mineserver.core.entry.HttpRequest;
import me.stevenkin.http.mineserver.core.entry.HttpResponse;
import me.stevenkin.http.mineserver.core.exception.AccessDirException;
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
        boolean showDir = Boolean.parseBoolean(ConfigUtil.getConfig("showDir", "false"));
        String file;
        int index = path.indexOf("?");
        int index1 = request.getPath().indexOf('?');
        file = path.substring(0,index<0?path.length():index);
        String dirPath = request.getPath().substring(0,index1<0?request.getPath().length() : index1);
        if (basePath.endsWith("/"))
            basePath = basePath.substring(0,basePath.length()-1);
        String filePath = basePath+"/"+file;
        File f = new File(filePath);
        if(!f.exists()){
            throw new NoFoundException();
        }
        if(f.isDirectory()){
            if(!dirPath.endsWith("/")){
                HttpResponse.redirect(response, request.getPath()+"/");
                return;
            }
            if (showDir){
                StringBuilder buf = new StringBuilder()
                        .append("<!DOCTYPE html>\r\n")
                        .append("<html><head><meta charset='utf-8' /><title>")
                        .append("File list: ")
                        .append(dirPath)
                        .append("</title></head><body>\r\n")
                        .append("<h3>File list: ")
                        .append(dirPath)
                        .append("</h3>\r\n")
                        .append("<ul>")
                        .append("<li><a href=\"../\">..</a></li>\r\n");
                for (File f1 : f.listFiles()) {
                    if (f1.isHidden() || !f1.canRead()) {
                        continue;
                    }
                    String name = f1.getName();
                    buf.append("<li><a href=\"")
                            .append(name)
                            .append("\">")
                            .append(name)
                            .append("</a></li>\r\n");
                }

                buf.append("</ul></body></html>\r\n");
                response.addHeader(CONTENT_TYPE, "text/html; charset=UTF-8");
                response.addHeader(CONNECTION, KEEP_ALIVE);
                response.getOutput().write(buf.toString().getBytes("UTF-8"));
                return;
            }
            throw new AccessDirException();
        }
        byte[] body = FileUtil.getFileContent(f);
        String mime = FileUtil.getFileMimeType(f);
        response.addHeader(CONTENT_TYPE, mime);
        response.setCode("200");
        response.setMessage("OK");
        Date lastModified = new Date(f.lastModified());
        response.addHeader(LAST_MODIFIED,
                formater.format(lastModified));
        response.addHeader(CONNECTION, KEEP_ALIVE);
        response.getOutput().write(body);
    }
}
