package me.stevenkin.http.mineserver.core;

import me.stevenkin.http.mineserver.core.entry.HttpRequest;
import me.stevenkin.http.mineserver.core.entry.HttpResponse;
import me.stevenkin.http.mineserver.core.processor.HttpParser;
import me.stevenkin.http.mineserver.core.task.HttpExchange;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by wjg on 16-4-14.
 */
public class MineServer implements Runnable {
    private static Logger logger = Logger.getLogger(MineServer.class);

    private String basePath;
    private int port;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private Map<SocketChannel,HttpParser> requestParserMap = new HashMap<SocketChannel,HttpParser>();

    public void init(int port,String basePath){
        this.port = port;
        this.basePath = basePath;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress("127.0.0.1",this.port));
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            logger.info("server is boot "+serverSocketChannel.toString());
        } catch (IOException e) {
            logger.error("server boot fail",e);
        }
    }

    @Override
    public void run(){
        while(true){
            try {
                int n = selector.select();
                if(n<=0)
                    continue;
            } catch (IOException e) {
                logger.error("server select error",e);
                continue;
            }
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                if(!key.isValid())
                    continue;
                try {
                    if (key.isAcceptable()) {
                        accept(key);
                    }
                    if (key.isReadable()) {
                        read(key);
                    }
                    if (key.isWritable()) {
                        write(key);
                    }
                }catch (Exception e){
                    if(key!=null&&key.isValid()){
                        key.cancel();
                        try {
                            key.channel().close();
                        } catch (IOException e1) {
                        }
                    }
                }
                iterator.remove();
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = channel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    public void read(SelectionKey key){
        SocketChannel channel = (SocketChannel) key.channel();
        HttpParser httpParser = requestParserMap.get(channel);
        if(httpParser==null){
            httpParser = new HttpParser(key,requestParserMap);
            requestParserMap.put(channel,httpParser);
        }
        HttpResponse response = null;
        try {
            if(httpParser.parse()){
                httpParser.clear();
                HttpRequest request = httpParser.getRequest();
                response = new HttpResponse();
                response.setRequest(request);
                HttpExchange httpExchange = new HttpExchange(request,response,key);
            }
        } catch (Exception e) {
            httpParser.clear();
            response = new HttpResponse();
            response.setCode("400");
            response.setProtocol("HTTP/1.1");
            response.setMessage("request syntax error");
            try {
                response.getOutput().write(new byte[0]);//TODO http error html
                byte[] headerBytes = response.headersToBytes();
                byte[] bodyBytes = response.getOutput().toByteArray();
                ByteBuffer responseBuffer = ByteBuffer.allocate(headerBytes.length+bodyBytes.length);
                responseBuffer.put(headerBytes).put(bodyBytes);
                responseBuffer.flip();
                key.attach(responseBuffer);
                key.interestOps(SelectionKey.OP_WRITE);
                selector.wakeup();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void write(SelectionKey key) throws IOException {
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        SocketChannel socketChannel = (SocketChannel) key.channel();
        socketChannel.write(buffer);
        if(!buffer.hasRemaining()){
            key.interestOps(SelectionKey.OP_READ);
            buffer.clear();
        }
    }

    public static void main(String[] args){
        MineServer server = new MineServer();
        int port = 8080;
        String basePath = "/home/wjg/server";
        if(args.length>0){
            port = Integer.parseInt(args[0]);
        }
        if(args.length>1){
            basePath = args[1];
        }
        server.init(port,basePath);
        new Thread(server).start();
    }


}
