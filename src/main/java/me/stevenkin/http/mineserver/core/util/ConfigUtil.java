package me.stevenkin.http.mineserver.core.util;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by wjg on 16-4-28.
 */
public class ConfigUtil{
    private static Properties properties = new Properties();

    public static void loadConfig(){
        InputStream inputStream = ConfigUtil.class.getClassLoader().getResourceAsStream("app.properties");
        properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static String getConfig(String key,String defaultValue){
        String value = properties.getProperty(key,defaultValue);
        return value;
    }
}
