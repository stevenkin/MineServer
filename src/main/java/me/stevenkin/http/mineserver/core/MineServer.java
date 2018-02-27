package me.stevenkin.http.mineserver.core;

import me.stevenkin.boomvc.ioc.Ioc;
import me.stevenkin.boomvc.ioc.IocFactory;
import me.stevenkin.http.mineserver.core.container.HttpContainer;
import me.stevenkin.http.mineserver.core.entry.HttpRequest;
import me.stevenkin.http.mineserver.core.entry.HttpResponse;
import me.stevenkin.http.mineserver.core.parser.HttpParser;
import me.stevenkin.http.mineserver.core.task.HttpExchange;
import me.stevenkin.http.mineserver.core.util.ConfigUtil;
import me.stevenkin.http.mineserver.core.util.ErrorMessageUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wjg on 16-4-14.
 */
public class MineServer implements Runnable {
    private int port;
    private int coreThreadCount;
    private String serverName;

    private String[] args;

    private Class<?> startClass;

    private String startPackage;

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private Map<SocketChannel,HttpParser> requestParserMap = new HashMap<SocketChannel,HttpParser>();
    private Ioc ioc;

    private ExecutorService service;
    private HttpContainer container;

    public void init(Class<?> startClass, String[] args){
        this.args = args;
        this.startClass = startClass;
        this.startPackage = startClass.getPackage().getName();
        this.ioc = IocFactory.buildIoc(Arrays.asList(startPackage));
        ConfigUtil.loadConfig();
        this.port = Integer.parseInt(ConfigUtil.getConfig("port","8080"));
        this.coreThreadCount = Integer.parseInt(ConfigUtil.getConfig("coreThreadCount","10"));
        this.serverName = ConfigUtil.getConfig("server","MineHttpServer");
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress("127.0.0.1",this.port));
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            service = Executors.newFixedThreadPool(this.coreThreadCount);
            container = new HttpContainer(this.ioc);
            System.out.println("server is boot "+serverSocketChannel.toString());
        } catch (IOException e) {
            System.err.println(e);
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
                System.err.println(e);
                continue;
            }
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
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
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = channel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key){
        SocketChannel channel = (SocketChannel) key.channel();
        HttpParser httpParser = requestParserMap.get(channel);
        if(httpParser==null){
            httpParser = new HttpParser(key,requestParserMap);
            requestParserMap.put(channel,httpParser);
        }
        HttpResponse response = new HttpResponse();
        response.addHeader("Server",this.serverName);
        try {
            if(httpParser.parse()){
                HttpRequest request = httpParser.getRequest();
                HttpExchange httpExchange = new HttpExchange(request,response,key,this.container,this.selector);
                service.execute(httpExchange);
                key.interestOps(key.interestOps()&(~SelectionKey.OP_READ));
                httpParser.clear();
            }
        } catch (Exception e) {
            httpParser.clear();
            response.setCode("400");
            response.setProtocol("HTTP/1.1");
            response.setMessage("request syntax error");
            try {
                response.getOutput().write(ErrorMessageUtil.ERROR_400.getBytes(Charset.forName("ISO-8859-1")));
                byte[] bodyBytes = response.getOutput().toByteArray();
                response.addHeader("Content-Length",Integer.toString(bodyBytes.length));
                byte[] headerBytes = response.headersToBytes();
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
        if(buffer==null||!buffer.hasRemaining())
            return ;
        SocketChannel socketChannel = (SocketChannel) key.channel();
        socketChannel.write(buffer);
        if(!buffer.hasRemaining()){
            key.interestOps(SelectionKey.OP_READ);
            buffer.clear();
        }
    }

    public static void run(Class<?> startClass, String[] args){
        MineServer mineServer = new MineServer();
        mineServer.init(startClass,args);
        new Thread((mineServer)).start();
    }


}
