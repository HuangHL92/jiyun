package com.ruoyi.area.edu.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.area.auth.domain.AuthClientDetails;
import com.ruoyi.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 标签表 edu_tag
 *
 * @author jiyunsoft
 * @date 2019-08-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("edu_tag")
public class Tag extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private String id;
    /**
     * 标签类型
     */
    private String type;
    /**
     * 标签编码
     */
    private String code;
    /**
     * 标签名称
     */
    private String name;
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    /**
     * 排序
     */
    private Integer orderNum = 0;

    /**
     * 原可访问客户端ids
     */
    @TableField(exist = false)
    private String oldClientIds;

    /**
     * 可访问客户端ids
     */
    @TableField(exist = false)
    private String clientIds;

    /**
     * 可访问客户端names
     */
    @TableField(exist = false)
    private String clientNames;

    /**
     * 辅助字段：checkbox是否没选
     */
    @TableField(exist = false)
    private boolean clientNull;

    @TableField(exist = false)
    private List<AuthClientDetails> clientDetailsList;
}
