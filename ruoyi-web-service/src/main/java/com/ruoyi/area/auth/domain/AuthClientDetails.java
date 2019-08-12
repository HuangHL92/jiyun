package com.ruoyi.area.auth.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 接入的客户端表 auth_client_details
 *
 * @author jiyunsoft
 * @date 2019-08-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("auth_client_details")
public class AuthClientDetails extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 接入方式(1：授权码；2：密码；3：扫码)
     */
    public static final String AUTH_SCOPE_CODE = "1";
    public static final String AUTH_SCOPE_PASSWORD = "2";
    public static final String AUTH_SCOPE_SCAN = "3";

    /**
     * id
     */
    @TableId
    private String id;
    /**
     * 接入的客户端ID
     */
    private String clientId;
    /**
     * 接入的客户端的名称
     */
    private String clientName;
    /**
     * 接入的客户端的密钥
     */
    private String clientSecret;
    /**
     * 回调地址
     */
    private String redirectUri;
    /**
     * 接入方式(1：授权码；2：密码；3：扫码)
     */
    private String scope;
    /**
     * 描述信息
     */
    private String description;
    /**
     * 状态（0：正常；1：停用）
     */
    private Integer status;
}
