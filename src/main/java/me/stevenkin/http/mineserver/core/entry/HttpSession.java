package me.stevenkin.http.mineserver.core.entry;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wjg on 16-4-27.
 */
public class HttpSession {
    private String sessionId;

    private Map<String,Object> attributes = new ConcurrentHashMap<>();

    public HttpSession(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Set<String> getAttributesKeys(){
        return this.attributes.keySet();
    }

    public void addAttribute(String key,Object value){
        attributes.put(key,value);
    }

    public Cookie getCookie(String domain,long maxAge,String path,boolean secure){
        Cookie cookie = new Cookie("jsessionid",this.sessionId);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(secure);
        return cookie;
    }
}
