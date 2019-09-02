package com.ruoyi.area.edu.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.base.BaseEntity;
import com.ruoyi.system.domain.SysDept;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 人员表 edu_employee
 *
 * @author jiyunsoft
 * @date 2019-08-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("edu_employee")
public class Employee extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 人员状态
     */
    public static final String EMPLOYEE_STATUS_NORMAL = "0";
    public static final String EMPLOYEE_STATUS_DISABLED = "1";
    public static final String EMPLOYEE_STATUS_OUT = "2";

    /**
     * id
     */
    @TableId
    private String id;
    /**
     * 管理用户id
     */
    private String userId;
    /**
     * 编号
     */
    private String eno;
    /**
     * 姓名
     */
    private String name;
    /**
     * 单位id
     */
    private String deptId;

    @TableField(exist = false)
    private SysDept dept;

    /**
     * 职务id
     */
    private String postId;

    @TableField(exist = false)
    private Tag post;

    /**
     * 标签
     */
    private String tags;
    /**
     * 联系电话
     */
    private String mobile;
    /**
     * 账号状态（0正常 1停用 2转出）
     */
    private String status;
    /**
     * 最后登录时间
     */
    private Date loginDate;
    /**
     * 排序
     */
    private Integer orderNum;
}
