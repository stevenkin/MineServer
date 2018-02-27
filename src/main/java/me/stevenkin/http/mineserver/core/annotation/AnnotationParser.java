package me.stevenkin.http.mineserver.core.annotation;

import me.stevenkin.http.mineserver.core.container.HttpHandle;
import me.stevenkin.http.mineserver.core.container.bean.MappingInfo;
import me.stevenkin.http.mineserver.core.parser.HttpParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wjg on 16-4-29.
 */
public class AnnotationParser {
    public static <T> MappingInfo parseAnnotation(Class<T> clazz){
        if(clazz.isAnnotationPresent(Controller.class)){
            Controller controller = clazz.getAnnotation(Controller.class);
            HttpParser.METHOD method = controller.method();
            String urlPatten = controller.urlPatten();
            InitParameter[] initParameters = controller.initParameters();
            urlPatten.replaceAll("\\*","(.+)");
            Map<String,String> map = new HashMap<>();
            for(InitParameter initParameter : initParameters){
                map.put(initParameter.key(),initParameter.value());
            }
            MappingInfo info = new MappingInfo(method,urlPatten,map);
            return info;
        }
        return null;
    }
}
