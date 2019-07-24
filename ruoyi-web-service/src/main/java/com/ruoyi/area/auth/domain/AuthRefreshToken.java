package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Refresh Token表 auth_refresh_token
 *
 * @author jiyunsoft
 * @date 2019-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("auth_refresh_token")
public class AuthRefreshToken extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private String id;
    /**
     * 表auth_access_token对应的Access Token记录
     */
    private String tokenId;
    /**
     * Refresh Token
     */
    private String refreshToken;
    /**
     * 过期时间戳
     */
    private Long expiresIn;
}
