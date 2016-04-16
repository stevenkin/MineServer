package me.stevenkin.http.mineserver.core.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

/**
 * Created by wjg on 16-4-16.
 */
public class FileUtil {
    public static String getFileMimeType(File file) throws Exception {
        URL url = file.toURL();
        URLConnection connection = url.openConnection();
        return connection.getContentType();
    }

    public static byte[] getFileContent(File file) throws Exception {
        return Files.readAllBytes(file.toPath());
    }
}
