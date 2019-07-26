package com.ruoyi.web.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 第三方认证配置类：吉运统一身份认证
 * @author tao.liang
 * @date 2019/3/26
 */
@Component
@ConfigurationProperties(prefix = "jiyun.oauth2")
@Data
public class JiYunOAuht2Config {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String authorizationUri;
    private String accessTokenUri;
    private String refreshTokenUri;
    private String userInfoUri;
}
