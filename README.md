# MineServer
===================================
>一个基于原生的nio实现的玩具级别的httpserver。仅实现*get/post*方法和部分http请求/响应头。之后将实现一个完整的webserver。
####目前实现的功能
* get/post方法
* 静态文件传输
* http 参数传递，支持url和请求体两种方式
* 支持长链接，不支持http pipeline
* 支持部分请求/响应头
* 支持cookie
* 支持session
* 支持用户实现HttpHandle接口，通过Controller注解产生动态web内容


####如何使用
* 下载`MineServer-core-jar-with-dependencies.jar`到本地目录，比如`/home/mineserver/test`
* 使用命令`mvn install:install-file -Dfile=/home/mineserver/testMineServer-core-jar-with-dependencies.jar -DgroupId=me.stevenkin.http -DartifactId=mineserver-core -Dversion=1.0-SNAPSHOT -Dpackaging=jar`将jar包安装到本地仓库
* 新建一个工程，用我提供的pom.xml构建，里面有必要的依赖和插件配置，如下：
```xml
  <dependency>
    <groupId>me.stevenkin.http</groupId>
    <artifactId>mineserver-core</artifactId>
    <version>1.0-SNAPSHOT</version>
  </dependency>
```
*在新建工程的resources目录添加`server.properties`,在里面写入您的配置,比如：
```
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


