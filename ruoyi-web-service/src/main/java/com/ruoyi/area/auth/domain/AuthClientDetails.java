package com.ruoyi.area.auth.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 接入的客户端表 auth_client_details
 *
 * @author jiyunsoft
 * @date 2019-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("auth_client_details")
public class AuthClientDetails extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private String id;
    /**
     * 接入的客户端ID
     */
    private String clientId;
    /**  */
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
     * 描述信息
     */
    private String description;
    /**
     * 0表示未开通；1表示正常使用；2表示已被禁用
     */
    private Integer status;
}
