package me.stevenkin.http.mineserver.core;

import me.stevenkin.http.mineserver.core.entry.HttpRequest;
import me.stevenkin.http.mineserver.core.entry.HttpResponse;
import me.stevenkin.http.mineserver.core.entry.ReadWriteBuffer;
import me.stevenkin.http.mineserver.core.processor.HttpParser;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
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

    /*private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ReadWriteBuffer buffer = (ReadWriteBuffer) key.attachment();
        if(buffer==null){
            buffer = new ReadWriteBuffer();
            key.attach(buffer);
        }
        int count = socketChannel.read(buffer.getReadBuffer());
        if(count<0){
            key.cancel();
            key.channel().close();
            return;
        }
        HttpParser httpParser = requestParserMap.get(socketChannel);
        if(httpParser ==null){
            httpParser = new HttpParser(socketChannel);
            requestParserMap.put(socketChannel, httpParser);
        }
        if(httpParser.parse(buffer.getReadBuffer(),count)) {
            key.interestOps(SelectionKey.OP_WRITE);
            HttpRequest request = httpParser.getRequest();
            HttpResponse response;
            try {
                response = HttpResponse.buildResponse(basePath,request.getPath());
            } catch (Exception e) {
                logger.error("server has a error",e);
                response = HttpResponse.buildError500Response();
            }
            byte[] header = response.getHeader();
            byte[] body = response.getBody();
            ByteBuffer writeBuffer = ByteBuffer.allocate(header.length+body.length);
            writeBuffer.put(header).put(body).flip();
            buffer.setWriteBuffer(writeBuffer);
            httpParser.clear();
        }
        buffer.getReadBuffer().clear();
        key.attach(buffer);
    }*/

    public void read(SelectionKey key){
        SocketChannel channel = (SocketChannel) key.channel();
        HttpParser httpParser = requestParserMap.get(channel);
        if(httpParser==null){
            httpParser = new HttpParser(key,requestParserMap);
            requestParserMap.put(channel,httpParser);
        }
        if(httpParser.parse()){
            ReadWriteBuffer buffer = (ReadWriteBuffer) key.attachment();
            if(buffer==null){
                buffer = new ReadWriteBuffer();
                key.attach(buffer);
            }
            buffer.setRequestHeaderBytes(httpParser.getRequestHeaderBytes());
            buffer.setRequestBodyBytes(httpParser.getRequestBodyBytes());
            HttpRequest request = httpParser.getRequest();
            HttpResponse response = new HttpResponse();
            response.setRequest(request);

        }
    }

    private void write(SelectionKey key) throws IOException {
        /*ReadWriteBuffer buffer = (ReadWriteBuffer) key.attachment();
        SocketChannel socketChannel = (SocketChannel) key.channel();
        socketChannel.write(buffer.getWriteBuffer());
        if(!buffer.getWriteBuffer().hasRemaining()){
            key.interestOps(SelectionKey.OP_READ);
            buffer.getWriteBuffer().clear();
        }*/
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
