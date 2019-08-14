package com.ruoyi.area.edu.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 标签_客户端关系表 edu_tag_client
 *
 * @author jiyunsoft
 * @date 2019-08-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("edu_tag_client")
public class TagClient extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private String id;
    /**
     * 标签类型
     */
    private String tagId;
    /**
     * 标签名称
     */
    private String clientId;
}
