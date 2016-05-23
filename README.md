# MineServer
===================================
>一个基于原生的nio实现的玩具级别的httpserver。仅实现*get/post*方法和部分http请求/响应头。之后将实现一个完整的webserver。

####目前实现的功能
* get/post方法
* 静态文件传输
* http 参数传递，支持url和请求体两种方式
* 支持长链接，暂不支持http pipeline
* 支持部分请求/响应头
* 支持cookie
* 支持session
* 支持用户实现HttpHandle接口，通过Controller注解产生动态web内容


####如何使用
* 下载`MineServer-core-jar-with-dependencies.jar`到本地目录，比如`/home/mineserver/test`
* 使用命令`mvn install:install-file -Dfile=/home/mineserver/test/MineServer-core-jar-with-dependencies.jar -DgroupId=me.stevenkin.http -DartifactId=mineserver-core -Dversion=1.0-SNAPSHOT -Dpackaging=jar`将jar包安装到本地仓库
* 新建一个工程，用我提供的pom.xml构建，里面有必要的依赖和插件配置，如下：
```xml
  <dependency>
    <groupId>me.stevenkin.http</groupId>
    <artifactId>mineserver-core</artifactId>
    <version>1.0-SNAPSHOT</version>
  </dependency>
```
* 在新建工程的resources目录添加`server.properties`,在里面写入您的配置,比如：
```java
#server的名字，在http响应头中使用
server = MineServer
#端口号
port = 8080
#server映射的本地目录
basePath = /home/wjg/server/
#主机名（这个暂时没什么用）
host = localhost
#server线程池中的线程数
coreThreadCount = 10
```
* 在`src`目录新建java文件，写入您的动态http实现类。要实现`HttpHandle`接口，用`Controller`进行注解（这点类似与`servlet 3.0`中的写法）
```java
@Controller(method = HttpParser.METHOD.GET,urlPatten = "/get",initParameters = {
        @InitParameter(key="key1",value="value1"),
        @InitParameter(key="key2",value="value2")
})
public class TestHandle extends AbstractHandle {
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
        char[] chars = {'h','e','l','l','o',',','w','o','r','l','d'};
        Writer writer = httpResponse.getWrite();
        writer.write(chars);
        writer.flush();
    }
}
```
* 使用`mvn clean package`打出jar包，比如`MineServer-test-jar-with-dependencies.jar`，然后使用`java -jar MineServer-test-jar-with-dependencies.jar`即可运行。


