package me.stevenkin.http.mineserver.core.entry;

import me.stevenkin.http.mineserver.core.container.HttpContext;
import me.stevenkin.http.mineserver.core.parser.HttpParser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wjg on 16-4-15.
 */
public class HttpRequest {
    private HttpParser.METHOD method;
    private String path;
    private String protocol;

    private String accept;
    private String acceptCharset;
    private String acceptEncoding;
    private String acceptLanguage;
    private String connection;
    private List<Cookie> cookies = new LinkedList<Cookie>();
    private long contentLength;
    private String contentType;
    private Date date;
    private String host;
    private String sessionId;

    private List<Header> headers = new LinkedList<Header>();
    private byte[] body = new byte[0];
    private Map<String,String> params = new HashMap<String,String>();

    private HttpSession session;

    private boolean isParseParams = false;
    private boolean isFirstGetSession = true;

    private HttpContext context;

    private Map<String,Object> attributes = new HashMap<>();

    public HttpRequest() {
    }

    public void setMethod(String method) {
        this.method = HttpParser.METHOD.methodOf(method);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getAcceptCharset() {
        return acceptCharset;
    }

    public void setAcceptCharset(String acceptCharset) {
        this.acceptCharset = acceptCharset;
    }

    public String getAcceptEncoding() {
        return acceptEncoding;
    }

    public void setAcceptEncoding(String acceptEncoding) {
        this.acceptEncoding = acceptEncoding;
    }

    public String getAcceptLanguage() {
        return acceptLanguage;
    }

    public void setAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void addCookies(Cookie cookie) {
        this.cookies.add(cookie);
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isFirstGetSession() {
        return isFirstGetSession;
    }

    public void setFirstGetSession(boolean firstGetSession) {
        isFirstGetSession = firstGetSession;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void addHeader(Header header) {
        this.headers.add(header);
    }

    public HttpParser.METHOD getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Map<String, String> getParams() {
        if(!this.isParseParams){
            this.parseParams();;
            this.isParseParams = true;
        }
        return params;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setContext(HttpContext context) {
        this.context = context;
    }

    public HttpSession getSession(boolean bool){
        if(this.session!=null)
            return this.session;
        this.session = null;
        if(bool) {
            if (isFirstGetSession) {
                this.session = this.context.getSessionManager().initSession();
                this.sessionId = session.getSessionId();
                Cookie cookie = this.session.getCookie(this.host,3600,this.path,false);
                this.context.getResponse().addSetCookie(cookie);
            } else {
                this.session = this.context.getSessionManager().getSession(this.sessionId);
            }
        }else{
            if(!isFirstGetSession){
                this.session = this.context.getSessionManager().getSession(this.sessionId);
            }
        }
        return this.session;
    }

    public HttpContext getContext() {
        return context;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Object getAttribute(String key){
        return this.attributes.get(key);
    }

    public void addAttributes(String key,Object value){
        this.attributes.put(key,value);
    }

    private void parseParams(){
        switch(method){
            case GET:
                int index = this.getPath().indexOf("?");
                int index1 = this.getPath().indexOf("#");
                if(index<0)
                    return ;
                else{
                    String queryStr = this.getPath().substring(index+1,index1<0?this.getPath().length():index1);
                    if(queryStr.length()>0){
                        try {
                            queryStr = URLDecoder.decode(queryStr,"UTF-8");
                            String[] paramPairs = queryStr.split("&");
                            for(String paramPair:paramPairs){
                                String[] pair = paramPair.split("=");
                                this.params.put(pair[0].trim(),pair[1].trim());
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case POST:
                if("application/x-www-form-urlencoded".equalsIgnoreCase(this.getContentType())&&this.getContentLength()>0){
                    try {
                        String queryStr = new String(this.getBody(),"ISO-8859-1");
                        queryStr = URLDecoder.decode(queryStr,"UTF-8");
                        String[] paramPairs = queryStr.split("&");
                        for(String paramPair:paramPairs){
                            String[] pair = paramPair.split("=");
                            this.params.put(pair[0].trim(),pair[1].trim());
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}
