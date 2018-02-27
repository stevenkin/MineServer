package me.stevenkin.http.mineserver.core.container;

import me.stevenkin.http.mineserver.core.entry.HttpRequest;
import me.stevenkin.http.mineserver.core.entry.HttpSession;
import me.stevenkin.http.mineserver.core.exception.InnerErrorException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wjg on 16-4-27.
 */
public class HttpSessionManager {
    private AtomicLong count = new AtomicLong(1L);
    private Map<String,HttpSession> sessionMap = new ConcurrentHashMap<>();

    public HttpSession getSession(String sessionId){
        HttpSession session = this.sessionMap.get(sessionId);
        if(session==null)
            throw new InnerErrorException();
        else
            return session;
    }

    public HttpSession initSession(){
        String sessionId = System.currentTimeMillis()+""+Thread.currentThread().getId()+""+count.getAndIncrement();
        HttpSession session = new HttpSession(sessionId);
        this.sessionMap.put(sessionId,session);
        return session;
    }
}
