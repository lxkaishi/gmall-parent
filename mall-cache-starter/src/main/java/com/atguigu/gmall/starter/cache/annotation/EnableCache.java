package com.atguigu.gmall.starter.cache.annotation;


import com.atguigu.gmall.starter.cache.MallCacheAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@EnableAspectJAutoProxy
@Import(MallCacheAutoConfiguration.class)
public @interface EnableCache {

}
