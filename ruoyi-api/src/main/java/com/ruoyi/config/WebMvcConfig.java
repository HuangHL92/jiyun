package com.ruoyi.config;

import com.ruoyi.interceptor.AuthAccessTokenInterceptor;
import com.ruoyi.interceptor.LoginInterceptor;
import com.ruoyi.interceptor.OauthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web相关配置
 * @author tao.liang
 * @date 2019/7/24
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	/**
	 * 添加拦截器
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/user/**","/oauth2.0/authorizePage","/oauth2.0/authorize","/sso/token");
		registry.addInterceptor(oauthInterceptor()).addPathPatterns("/oauth2.0/authorize");
		registry.addInterceptor(accessTokenInterceptor()).addPathPatterns("/api/**");
	}

	@Bean
	public OauthInterceptor oauthInterceptor(){
		return new OauthInterceptor();
	}

	@Bean
	public AuthAccessTokenInterceptor accessTokenInterceptor(){
	    return new AuthAccessTokenInterceptor();
    }

}
