package com.ruoyi.area.edu.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
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
    @Excel(name = "学号", prompt = "必填")
    private String sno;
    /**
     * 姓名
     */
    @Excel(name = "姓名")
    private String name;
    /**
     * 学校id
     */
    @Excel(name = "学校")
    private String deptId;
    /**
     * 学校名称
     */
    @TableField(exist = false)
    private String deptName;
    /**
     * 年级
     */
    @Excel(name = "年级")
    private String grade;

    /** 班级 */
    @TableField(value = "class")
    @Excel(name = "班级")
    private Integer classStr;
    /**
     * 账号状态（0正常 1停用）
     */
    @Excel(name = "账号状态",readConverterExp = "0=正常,1=停用",combo = {"正常","停用"})
    private String status;
    /**
     * 标签
     */
    private String tags;
}
