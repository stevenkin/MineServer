package me.stevenkin.mineapp;

import me.stevenkin.boomvc.ioc.annotation.Bean;
import me.stevenkin.http.mineserver.core.annotation.Controller;
import me.stevenkin.http.mineserver.core.annotation.InitParameter;
import me.stevenkin.http.mineserver.core.container.AbstractHandle;
import me.stevenkin.http.mineserver.core.entry.HttpRequest;
import me.stevenkin.http.mineserver.core.entry.HttpResponse;
import me.stevenkin.http.mineserver.core.parser.HttpParser;

import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

@Controller(method = HttpParser.METHOD.GET,urlPatten = "/get",initParameters = {
        @InitParameter(key="key1",value="value1"),
        @InitParameter(key="key2",value="value2")
})
public class TestController extends AbstractHandle {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        Map<String,String> params = httpRequest.getParams();
        for(Map.Entry<String,String> entry : params.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
        System.out.println();
        Iterator<String> iterator = getInitParameterNames();
        while(iterator.hasNext()){
            String key = iterator.next();
            System.out.println(key+":"+getInitParameter(key));
        }
        char[] chars = {'h','e','l','l','o',',','w','o','r','l','d','!'};
        Writer writer = httpResponse.getWrite();
        writer.write(chars);
        writer.flush();
    }
}
