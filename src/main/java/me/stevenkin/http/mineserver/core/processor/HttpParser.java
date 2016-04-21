package me.stevenkin.http.mineserver.core.processor;

import me.stevenkin.http.mineserver.core.entry.HttpRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created by wjg on 16-4-14.
 */
public class HttpParser {
    private static final byte[] END = new byte[] { 13, 10, 13, 10 };

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

    private boolean isParseRequestHeader = false;

    private SocketChannel socketChannel;

    private byte[] headerBytes = new byte[0];
    private byte[] bodyBytes = new byte[0];

    private METHOD method;
    private String path;
    private String protocol;

    public HttpParser(SocketChannel socketChannel){
        this.socketChannel = socketChannel;
    }

    public boolean parse(ByteBuffer byteBuffer,int count) throws IOException {
        if(isParseRequestHeader)
            return isParseRequestHeader;
        if(count<=0) {
            return isParseRequestHeader;
        }else{
            byte[] bytes = new byte[count];
            System.arraycopy(byteBuffer.array(),0,bytes,0,count);
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            arrayOutputStream.write(headerBytes);
            arrayOutputStream.write(bytes);
            headerBytes = arrayOutputStream.toByteArray();
            int index = findIndex(headerBytes,END);
            if(index<0){
                isParseRequestHeader = false;
            }else{
                isParseRequestHeader = true;
                String headerStr = new String(headerBytes, Charset.forName("ISO-8859-1"));
                String[] heads = headerStr.split("\r\n");
                String[] lines = heads[0].split(" ");
                method = METHOD.methodOf(lines[0]);
                path = lines[1];
                protocol = lines[2];
            }
            return isParseRequestHeader;
        }
    }

    public HttpRequest getRequest(){
        return new HttpRequest(this.method,this.path,this.protocol);
    }

    public void clear(){
        this.isParseRequestHeader = false;
        this.method = null;
        this.path = null;
        this.protocol = null;
        this.headerBytes = new byte[0];
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
}
