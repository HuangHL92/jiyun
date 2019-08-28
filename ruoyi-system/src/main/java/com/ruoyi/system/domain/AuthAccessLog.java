package com.ruoyi.system.domain;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.util.Date;

/**
 * 客户端访问日志表 auth_access_log
 *
 * @author jiyunsoft
 * @date 2019-08-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("auth_access_log")
public class AuthAccessLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    @TableId
    private String id;
    /** 客户端id */
    private String clientId;
    /** 登录账号 */
    private String loginName;
    /** 来源主机 */
    private String ipAddr;
    /** 登录状态（0成功 1失败） */
    private Integer status;
    /** 登录时间 */
    private Date loginTime;
    @TableField(exist = false)
    private String clientName;
}


