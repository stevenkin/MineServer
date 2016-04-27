package me.stevenkin.http.mineserver.core.parser;

import me.stevenkin.http.mineserver.core.entry.Cookie;
import me.stevenkin.http.mineserver.core.entry.Header;
import me.stevenkin.http.mineserver.core.entry.HttpRequest;
import me.stevenkin.http.mineserver.core.exception.ProtocolSyntaxException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Created by wjg on 16-4-14.
 */
public class HttpParser {
    private static final DateFormat formater = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
    private static final byte[] END = new byte[] { 13, 10, 13, 10 };

    private boolean isParseRequestHeader = false;
    private boolean isGet = true;
    private boolean isHttp11 = true;
    private boolean isKeepAlive = true;

    private byte[] headerBytes = new byte[0];
    private byte[] bodyBytes = new byte[0];

    private HttpRequest request;

    private SelectionKey key;

    private Map<SocketChannel,HttpParser> map;

    public HttpParser(SelectionKey key, Map<SocketChannel,HttpParser> map){
        this.key = key;
        this.map = map;
    }

    public boolean parse() throws Exception {
        if(!isParseRequestHeader){
            byte[] bytes = readBytes();
            if(bytes==null) {
                map.remove(this.key.channel());
                return false;
            }
            ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
            outputStream1.write(headerBytes);
            outputStream1.write(bytes);
            byte[] sourceBytes = outputStream1.toByteArray();
            int index = findIndex(sourceBytes,END);
            if(index<0) {
                this.headerBytes = sourceBytes;
                return false;
            }else{
                this.isParseRequestHeader = true;
                this.headerBytes = new byte[index];
                System.arraycopy(sourceBytes,0,this.headerBytes,0,index);
                parseHeaders();
                if((sourceBytes.length-index-4)>0){
                    this.bodyBytes = new byte[sourceBytes.length-index-4];
                    System.arraycopy(sourceBytes,index,this.bodyBytes,0,sourceBytes.length-index);
                    if(isGet)
                        throw new ProtocolSyntaxException("http protocol parse syntax error!");
                }else{
                    if(this.isGet)
                        return true;
                }
            }
        }else{
            byte[] bytes = readBytes();
            if(bytes==null) {
                map.remove(key.channel());
                return false;
            }
            if(bytes.length>0) {
                if (this.isGet) {
                    throw new ProtocolSyntaxException("http protocol parse syntax error!");
                } else {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    outputStream.write(this.bodyBytes);
                    outputStream.write(bytes);
                    byte[] byteArray = outputStream.toByteArray();
                    long length = this.getRequest().getContentLength();
                    if (byteArray.length >= length) {
                        byte[] bytes1 = new byte[(int) length];
                        System.arraycopy(byteArray, 0, bytes1, 0, (int) length);
                        this.bodyBytes = bytes1;
                        this.request.setBody(this.bodyBytes);
                    /*if(byteArray.length-length>0){// TODO http pipeline
                        byte[] bytes2 = new byte[(int)(byteArray.length-length)];
                        System.arraycopy(byteArray,(int)length,bytes2,0,bytes2.length);
                        this.headerBytes = bytes2;
                    }*/
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private byte[] readBytes() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int count = 0;
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        do{
            byteBuffer.clear();
            SocketChannel socketChannel = (SocketChannel) this.key.channel();
            count = socketChannel.read(byteBuffer);
            if(count>=0) {
                byte[] bytes = new byte[count];
                System.arraycopy(byteBuffer.array(),0,bytes,0,count);
                outputStream.write(bytes);
            }else{
                this.key.cancel();
                this.key.channel().close();
                return null;
            }
        }while(count>0);
        return outputStream.toByteArray();
    }

    private void parseHeaders() throws Exception {
        this.request = new HttpRequest();
        String headerStr = new String(this.headerBytes, Charset.forName("ISO-8859-1"));
        String[] headers = headerStr.trim().split("\r\n");
        String[] lines = headers[0].split("\\s+");
        request.setMethod(lines[0].trim());
        request.setPath(lines[1].trim());
        if(request.getPath().startsWith("http://")){
            int index1 = request.getPath().indexOf("/",7);
            if(index1<0)
                request.setPath("/");
            else{
                request.setPath(request.getPath().substring(index1));
            }
        }
            request.setPath(request.getPath().substring(7));
        request.setProtocol(lines[2].trim());
        for(int i=1;i<headers.length;i++){
            String[] headerPair = headers[i].split(":");
            String name = headerPair[0].trim().toLowerCase();
            String value = headerPair[1].trim();
            request.setHeaders(new Header(name,value));
            switch(name){
                case "accept":
                    request.setAccept(value);
                    break;
                case "accept-charset":
                    request.setAcceptCharset(value);
                    break;
                case "accept-encoding":
                    request.setAcceptEncoding(value);
                    break;
                case "accept-language":
                    request.setAcceptLanguage(value);
                    break;
                case "connection":
                    request.setConnection(value);
                    break;
                case "cookie":
                    String[] cookieStrs = value.split("; ");
                    for(String cookieStr:cookieStrs){
                        cookieStr = cookieStr.trim();
                        String[] cookiePair = cookieStr.split("=");
                        String cookieName = cookiePair[0].trim();
                        String cookieValue = cookiePair[1].trim();
                        request.addCookies(new Cookie(cookieName,cookieValue));
                    }
                    break;
                case "content-length":
                    request.setContentLength(Long.parseLong(value));
                    break;
                case "content-type":
                    request.setContentType(value);
                    break;
                case "date":
                    request.setDate(formater.parse(value));
                    break;
                case "host":
                    request.setHost(value);
                    break;
            }
        }
        if(METHOD.GET==request.getMethod()){
            this.isGet = false;
        }else{
            this.isGet = false;
        }
        if(request.getProtocol().equalsIgnoreCase("HTTP/1.1")){
            this.isHttp11 = true;
            if(request.getConnection().equalsIgnoreCase("keep-alive")){
                this.isKeepAlive = true;
            }else{
                this.isKeepAlive = false;
            }
        }else{
            this.isHttp11 = false;
            this.isKeepAlive = false;
        }
    }

    public HttpRequest getRequest(){
        return this.request;
    }

    public void clear(){
        this.isParseRequestHeader = false;
        this.isGet = true;
        this.isHttp11 = true;
        this.isKeepAlive = true;

        this.headerBytes = new byte[0];//TODO http pipeline
        this.bodyBytes = new byte[0];
        this.request = null;
    }

    private int findIndex(byte[] source, byte[] bytes){
    outor:for(int i=0;i<=source.length-bytes.length;i++){
            int j;
            for(j=0;j<bytes.length;j++){
                if(source[i] == bytes[j]){
                    i++;
                }else{
                    i-=j;
                    continue outor;
                }
            }
            if(j==bytes.length){
                return i-j;
            }
        }
        return -1;
    }

    public enum METHOD{
        GET("GET"),POST("POST");
        private String method;

        METHOD(String method){
            this.method = method;
        }

        public String getMethod(){
            return this.method;
        }

        public static METHOD methodOf(String method){
            METHOD method1 = null;
            switch(method){
                case "GET":
                    method1 = GET;
                    break;
                case "POST":
                    method1 = POST;
                    break;
                default:
                    method1 = GET;
                    break;
            }
            return method1;
        }
    }

    public byte[] getHeaderBytes() {
        return headerBytes;
    }

    public byte[] getBodyBytes() {
        return bodyBytes;
    }
}
