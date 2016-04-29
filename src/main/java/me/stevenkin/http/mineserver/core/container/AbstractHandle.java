package me.stevenkin.http.mineserver.core.container;

import me.stevenkin.http.mineserver.core.container.bean.HttpInitConfig;

import java.util.Iterator;

/**
 * Created by wjg on 16-4-26.
 */
public abstract class AbstractHandle implements HttpHandle {
    private HttpInitConfig initConfig = new HttpInitConfig();

    @Override
    public void init(HttpInitConfig config) {
        this.initConfig = config;
    }

    @Override
    public void destroy() {

    }

    public Iterator<String> getInitParameterNames(){
        return this.initConfig.getInitParameterNames();
    }

    public String getInitParameter(String key){
        return this.initConfig.getInitParameter(key);
    }


}
