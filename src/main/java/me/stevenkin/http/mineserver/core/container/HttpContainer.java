package me.stevenkin.http.mineserver.core.container;

import me.stevenkin.boomvc.ioc.Ioc;
import me.stevenkin.http.mineserver.core.container.bean.HttpContext;
import me.stevenkin.http.mineserver.core.entry.HttpRequest;
import me.stevenkin.http.mineserver.core.entry.HttpResponse;
import me.stevenkin.http.mineserver.core.util.ConfigUtil;

/**
 * Created by wjg on 16-4-24.
 */
public class HttpContainer {
    private MappingHandle mappingHandle;

    private HttpSessionManager sessionManager;

    public HttpContainer(Ioc ioc) {
        this.mappingHandle = new MappingHandle(ioc);
        this.sessionManager = new HttpSessionManager();
    }

    public void doProcess(HttpRequest request, HttpResponse response) throws Exception{
        HttpContext context = new HttpContext(request,response,this.getSessionManager());
        request.setContext(context);
        response.setContext(context);
        response.setRequest(request);
        HttpHandle handle = mappingHandle.getHander(request);
        handle.service(request,response);
    }

    public HttpSessionManager getSessionManager() {
        return sessionManager;
    }
}
