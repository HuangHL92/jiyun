package com.ruoyi.area.auth.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Access Token表 auth_access_token
 *
 * @author jiyunsoft
 * @date 2019-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("auth_access_token")
public class AuthAccessToken extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private String id;
    /**
     * Access Token
     */
    private String accessToken;
    /**
     * 关联的用户ID
     */
    private String userId;
    /**
     * 接入的客户端ID
     */
    private String clientId;
    /**
     * 过期时间戳
     */
    private Long expiresIn;
    /**
     * 授权类型，比如：authorization_code
     */
    private String grantType;
}
