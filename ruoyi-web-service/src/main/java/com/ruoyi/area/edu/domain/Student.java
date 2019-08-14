package com.ruoyi.area.edu.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 学生表 edu_student
 *
 * @author jiyunsoft
 * @date 2019-08-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("edu_student")
public class Student extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private String id;
    /**
     * 对应的用户id
     */
    private String userId;
    /**
     * 学号
     */
    private String sno;
    /**
     * 姓名
     */
    private String name;
    /**
     * 学校id
     */
    private String deptId;
    /**
     * 年级
     */
    private String grade;

    /** 班级 */
    @TableField(value = "class")
    private Integer classStr;
    /**
     * 帐号状态（0正常 1停用）
     */
    private String status = "0";
    /**
     * 标签
     */
    private String tags;
}
