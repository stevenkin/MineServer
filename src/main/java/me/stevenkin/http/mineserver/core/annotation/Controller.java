package me.stevenkin.http.mineserver.core.annotation;

import me.stevenkin.boomvc.ioc.annotation.Bean;
import me.stevenkin.http.mineserver.core.parser.HttpParser;

import java.lang.annotation.*;

import static me.stevenkin.http.mineserver.core.parser.HttpParser.METHOD.GET;

/**
 * Created by wjg on 16-4-29.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Bean
public @interface Controller {
    HttpParser.METHOD method() default GET;

    String urlPatten() default "/*";

    InitParameter[] initParameters() default {};

}
