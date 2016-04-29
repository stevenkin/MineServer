package me.stevenkin.http.mineserver.core.container.bean;

import me.stevenkin.http.mineserver.core.parser.HttpParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wjg on 16-4-29.
 */
public class MappingInfo {
    private HttpParser.METHOD method;
    private String urlPatten;
    private Map<String,String> initParameter = new HashMap<>();

    public MappingInfo(HttpParser.METHOD method, String urlPatten, Map<String, String> initParameter) {
        this.method = method;
        this.urlPatten = urlPatten;
        this.initParameter = initParameter;
    }

    public HttpParser.METHOD getMethod() {
        return method;
    }

    public void setMethod(HttpParser.METHOD method) {
        this.method = method;
    }

    public String getUrlPatten() {
        return urlPatten;
    }

    public void setUrlPatten(String urlPatten) {
        this.urlPatten = urlPatten;
    }

    public Map<String, String> getInitParameter() {
        return initParameter;
    }

    public void setInitParameter(Map<String, String> initParameter) {
        this.initParameter = initParameter;
    }

    public void addInitParameter(String key,String value){
        this.initParameter.put(key,value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MappingInfo that = (MappingInfo) o;

        if (method != that.method) return false;
        return urlPatten.equals(that.urlPatten);

    }

    @Override
    public int hashCode() {
        int result = method.hashCode();
        result = 31 * result + urlPatten.hashCode();
        return result;
    }
}
