package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 日历表 sys_calendar
 * 
 * @author jiyunsoft
 * @date 2019-03-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_calendar")
public class SysCalendar extends BaseEntity
{
    private static final long serialVersionUID = 1L;

	/** 数据标识 */
    @TableId
	private String id;
	/** 年度 */
	private Integer years;
	/** 日期 */
    private Integer days;

	@TableField(exist = false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Excel(name = "日期",width = 25,dateFormat = "yyyy-MM-dd",type = Excel.Type.ALL)
	private Date dateStr;

	/** 日期类型 */
	@Excel(name = "日期类型",readConverterExp = "3=节假日,4=工作日",width = 25,type = Excel.Type.EXPORT)
    private Integer dayType;

	@TableField(exist = false)
	@Excel(name = "日期类型(节假日/工作日)",readConverterExp = "3=节假日,4=工作日",width = 25,type = Excel.Type.IMPORT,combo = {"节假日","工作日"})
	private String type;
}
