package me.stevenkin.http.mineserver.core.container;

import me.stevenkin.http.mineserver.core.container.bean.HttpInitConfig;

/**
 * Created by wjg on 16-4-26.
 */
public abstract class AbstractHandle implements HttpHandle {
    private HttpInitConfig initConfig;

    @Override
    public void init(HttpInitConfig config) {
        this.initConfig = config;
    }

    @Override
    public void destroy() {

    }
}
