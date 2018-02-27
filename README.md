 MineServer
===================================
>一个基于原生的nio实现的httpserver。

目前实现的功能
* get/post方法
* 静态文件传输
* http 参数传递，支持url和请求体两种方式
* 支持长链接，暂不支持http pipeline
* 支持部分请求/响应头
* 支持cookie
* 支持session
* 支持用户实现HttpHandle接口，通过Controller注解产生动态web内容


如何使用
- 项目用`maven`构建,请确保已安装maven，然后使用下面命令。
```xml
1. git clone git@github.com:StevenKin/MineServer.git
2. git clone git@github.com:StevenKin/Boomvc.git
3. cd MineServer & mvn clean install
4. cd Boomvc/boomvc-ioc & mvn clean install
```
- 加入依赖和配置到pom.xml
```xml
<dependency>
        <groupId>me.stevenkin.http</groupId>
        <artifactId>mineserver-core</artifactId>
        <version>0.1</version>
</dependency>
```
- 写一个controller控制器
```java
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
        char[] chars = {'h','e','l','l','o',',','w','o','r','l','d'};
        Writer writer = httpResponse.getWrite();
        writer.write(chars);
        writer.flush();
    }
}
```
这个控制器就可以输出helloworld啦...
- 然后我们要写一个配置文件`app.properties`
```xml
server = MineServer
port = 8080
showDir = true
basePath = D:/workspace/
host = localhost
coreThreadCount = 10
```
配置很简单，我来解释一下配置的意思
```xml
server : 服务器名，是服务器监听的端口
port : 服务器监听的端口
showDir : 访问静态文件时如果是目录是否显示
basePath : 静态路径映射的基目录
host : 主机名
coreThreadCount : 处理请求的线程池大小
```
- 写一个Main类来启动应用
```java
public class MineApplication {

    public static void main(String[] args){
        MineServer.run(MineApplication.class, args);
    }

}
```
- 如何运行？添加下面配置到`pom.xml`里
```xml
<build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
            <plugin>
                <artifactId>maven-assembly-plugin</artifactId>
                <configuration>
                    <descriptorRefs>
                        <descriptorRef>jar-with-dependencies</descriptorRef>
                    </descriptorRefs>
                    <archive>
                        <manifest>
                            <mainClass>me.stevenkin.mineapp.MineApplication</mainClass>
                        </manifest>
                    </archive>
                </configuration>
                <executions>
                    <execution>
                        <id>make-assembly</id> <!-- this is used for inheritance merges -->
                        <phase>package</phase> <!-- bind to the packaging phase -->
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```
使用命令`mvn clean package`打出jar包，然后`Java -jar app.jar`就可以运行了，也可以到MineServer/example里看写好的示例。
