package me.stevenkin.http.mineserver.core.container;

import me.stevenkin.http.mineserver.core.container.bean.HttpInitConfig;
import me.stevenkin.http.mineserver.core.entry.HttpRequest;
import me.stevenkin.http.mineserver.core.entry.HttpResponse;

/**
 * Created by wjg on 16-4-26.
 */
public interface HttpHandle {
    void init(HttpInitConfig config);
    void service(HttpRequest request, HttpResponse response)  throws Exception;
    void destroy();
}
