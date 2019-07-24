package com.ruoyi.common.annotation;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description 接口权限注解
 * @Author yufei
 * @Date 2019-03-06 07:59
 **/
@Target(ElementType.METHOD)//这个注解是应用在方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateRequest {
    /**
     * 是否需要验证权限
     * @return
     */
    boolean required() default true;

    /**
     * 验证内容(request参数 header头部)
     * @return
     */
    String vtype() default "request";
}