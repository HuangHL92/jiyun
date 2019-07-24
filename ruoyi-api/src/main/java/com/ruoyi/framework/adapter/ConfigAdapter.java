package com.ruoyi.framework.adapter;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class ConfigAdapter extends WebMvcConfigurationSupport {
    @Override
    public void addViewControllers(ViewControllerRegistry viewControllerRegistry) {
        viewControllerRegistry.addViewController("/").setViewName("index");
        //设置ViewController的优先级,将此处的优先级设为最高,当存在相同映射时依然优先执行
        viewControllerRegistry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        super.addViewControllers(viewControllerRegistry);
    }

//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        super.configureMessageConverters(converters);
//
//        // 1.需要先定义一个convert 转换消息的对象
//        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
//        // 2.添加fastJson的配置信息,比如，是否需要格式化返回的json数据
//        FastJsonConfig fastJsonConfig = new FastJsonConfig();
//        // 空值特别处理
//        // WriteNullListAsEmpty 将Collection类型字段的字段空值输出为[]
//        // WriteNullStringAsEmpty 将字符串类型字段的空值输出为空字符串 ""
//        // WriteNullNumberAsZero 将数值类型字段的空值输出为0
//        // WriteNullBooleanAsFalse 将Boolean类型字段的空值输出为false
//        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,
//                SerializerFeature.WriteMapNullValue,
//                SerializerFeature.WriteNullNumberAsZero,
//                SerializerFeature.WriteNullStringAsEmpty,
//                SerializerFeature.WriteNullBooleanAsFalse,
//                SerializerFeature.DisableCircularReferenceDetect,
//                SerializerFeature.WriteDateUseDateFormat,
//                SerializerFeature.SortField);
//        // 处理中文乱码问题
//        List<MediaType> fastMediaTypes = new ArrayList<>();
//        fastJsonConfig.setCharset(Charset.forName("UTF-8"));
//        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
//        fastConverter.setSupportedMediaTypes(fastMediaTypes);
//
//        //处理字符串, 避免直接返回字符串的时候被添加了引号
//        StringHttpMessageConverter smc = new StringHttpMessageConverter(Charset.forName("UTF-8"));
//        converters.add(smc);
//
//        //统一配置日期格式
//        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        //驼峰转下划线
////        SerializeConfig serializeConfig=new SerializeConfig();
////        serializeConfig.propertyNamingStrategy= PropertyNamingStrategy.PascalCase;
////        fastJsonConfig.setSerializeConfig(serializeConfig);
//
//
//        // 3.在convert中添加配置信息
//        fastConverter.setFastJsonConfig(fastJsonConfig);
//        // 4.将convert添加到converters当中
//        converters.add(fastConverter);
//
//    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}