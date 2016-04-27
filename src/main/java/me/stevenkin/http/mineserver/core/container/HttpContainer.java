package me.stevenkin.http.mineserver.core.container;

import me.stevenkin.http.mineserver.core.entry.HttpRequest;
import me.stevenkin.http.mineserver.core.entry.HttpResponse;
import me.stevenkin.http.mineserver.core.entry.HttpSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wjg on 16-4-24.
 */
public class HttpContainer {
    private MappingHandle mappingHandle;

    private Map<String,HttpSession> sessionMap = new ConcurrentHashMap<>();

    public HttpContainer() {
        this.mappingHandle = new MappingHandle();
    }

    public void doProcess(HttpRequest request, HttpResponse response) throws Exception{
        HttpHandle handle = mappingHandle.getHander(request);
        handle.service(request,response);
    }
}
