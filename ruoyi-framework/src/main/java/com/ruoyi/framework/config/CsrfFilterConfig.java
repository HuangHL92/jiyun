package com.ruoyi.framework.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

/**
 * CSRF Filter
 *
 * @author tao.liang
 * @date 2019年8月20日
 */
@Configuration
public class CsrfFilterConfig {
    @Bean
    public FilterRegistrationBean csrfFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CsrfFilter(new HttpSessionCsrfTokenRepository()));
        registration.addUrlPatterns("/*");
        return registration;
    }
}
