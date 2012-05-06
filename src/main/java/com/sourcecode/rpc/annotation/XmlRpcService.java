package com.sourcecode.rpc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * XmlRpcService.java
 * 
 * @author baojun
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface XmlRpcService {

    /**
     * value为空则xml rpc service的名称默认使用sping bean的id。否则使用value
     */
    String value() default "";

    /**
     * 是否使用方法注解
     * 
     * @return false:服务组件中的所有公共方法都作为xml rpc的服务方法 <br>
     *         true:在服务组件具有XmlRpcMethod的方法才暴露为xmlrpc
     */
    boolean useMethodAnnotation() default true;
}
