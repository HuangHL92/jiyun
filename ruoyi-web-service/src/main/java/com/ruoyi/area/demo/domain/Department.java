package com.ruoyi.area.demo.domain;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 练习_部门表 biz_department
 * 
 * @author jiyunsoft
 * @date 2019-02-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("biz_department")
public class Department extends BaseEntity
{
    private static final long serialVersionUID = 1L;

	/** id */
    @TableId
	private String id;
	/** 部门名称 */
	private String name;
	/** 部门级别 */
	private String grade;
	/** 创办日期 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date establishmentDate;
	/** 联系人 */
	private String contact;
	/** 联系电话 */
	private String contactNumber;
	/** 联系地址 */
	private String contactAddress;
}
