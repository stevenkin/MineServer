package me.stevenkin.http.mineserver.core.container;

import me.stevenkin.http.mineserver.core.annotation.AnnotationParser;
import me.stevenkin.http.mineserver.core.annotation.Controller;
import me.stevenkin.http.mineserver.core.container.bean.HttpInitConfig;
import me.stevenkin.http.mineserver.core.container.bean.MappingInfo;
import me.stevenkin.http.mineserver.core.entry.HttpRequest;
import me.stevenkin.http.mineserver.core.exception.NoFoundException;
import me.stevenkin.http.mineserver.core.parser.HttpParser;
import me.stevenkin.http.mineserver.core.util.ClassUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wjg on 16-4-26.
 */
public class MappingHandle {
    private Map<String,HttpHandle> staticHandleMap = new ConcurrentHashMap<>();
    private Map<MappingInfo,ClassPair<? extends HttpHandle>> handleMap = new ConcurrentHashMap<>();

    public MappingHandle(){
        this.init();
    }

    public void init(){
        MappingInfo staticMappingInfo = new MappingInfo(HttpParser.METHOD.GET,"^/static/(.+)",new HashMap<>());
        HttpInitConfig config1 = new HttpInitConfig();
        config1.putAllInitParameter(staticMappingInfo.getInitParameter());
        ClassPair<HttpStaticHandle> staticHandleClassPair = new ClassPair<>(HttpStaticHandle.class,config1);
        handleMap.put(staticMappingInfo,staticHandleClassPair);
        List<Class<? extends HttpHandle>> classList = ClassUtil.getClassListByAnnotation("", Controller.class);
        for(Class<? extends HttpHandle> clazz : classList){
            MappingInfo info = AnnotationParser.parseAnnotation(clazz);
            HttpInitConfig config = new HttpInitConfig();
            config.putAllInitParameter(info.getInitParameter());
            ClassPair<? extends HttpHandle> classPair = new ClassPair<>(clazz,config);
            handleMap.put(info,classPair);
        }
    }

    public HttpHandle getHander(HttpRequest request) throws Exception {
        String path = request.getPath();
        int index = path.indexOf("?");
        path = path.substring(0,index<0?path.length():index);
        for(MappingInfo info : this.handleMap.keySet()){
            Pattern p = Pattern.compile(info.getUrlPatten());
            Matcher matcher = p.matcher(path);
            if(matcher.matches()&&info.getMethod()==request.getMethod()) {
                List<String> matcherStrList = new ArrayList<>();
                for(int i=1;i<=matcher.groupCount();i++){
                    matcherStrList.add(matcher.group(i));
                }
                request.addAttributes("matcherStrList",matcherStrList);
                return handleMap.get(info).getInstance();
            }
        }
        throw new NoFoundException();
    }

    public static class ClassPair<T extends HttpHandle>{
        private Class<T> clazz;
        private T instance;
        private HttpInitConfig config;

        public ClassPair(Class<T> clazz,HttpInitConfig config) {
            this.clazz = clazz;
            this.config = config;
        }

        public Class<T> getClazz() {
            return clazz;
        }

        public void setClazz(Class<T> clazz) {
            this.clazz = clazz;
        }

        public synchronized T getInstance() throws Exception {
            if(this.instance==null){
                this.instance = this.clazz.newInstance();
                this.instance.init(config);
            }
            return this.instance;
        }
    }
}
