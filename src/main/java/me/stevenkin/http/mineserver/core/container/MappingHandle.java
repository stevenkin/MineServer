package me.stevenkin.http.mineserver.core.container;

import me.stevenkin.http.mineserver.core.entry.HttpRequest;
import me.stevenkin.http.mineserver.core.exception.NoFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wjg on 16-4-26.
 */
public class MappingHandle {
    private Map<String,HttpHandle> handleMap = new ConcurrentHashMap<>();

    public MappingHandle(){
        this.init();
    }

    public void init(){
        handleMap.put("^/static/.+",new HttpStaticHandle());
    }

    public HttpHandle getHander(HttpRequest request){
        String path = request.getPath();
        for(String regexStr:this.handleMap.keySet()){
            Pattern p = Pattern.compile(regexStr);
            Matcher matcher = p.matcher(path);
            if(matcher.matches())
                return handleMap.get(regexStr);
        }
        throw new NoFoundException();
    }
}
