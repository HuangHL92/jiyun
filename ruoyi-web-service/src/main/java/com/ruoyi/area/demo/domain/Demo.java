package com.ruoyi.area.demo.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 测试表 sys_demo
 * 
 * @author ruoyi
 * @date 2019-01-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_demo")
public class Demo extends BaseEntity
{
	private static final long serialVersionUID = 1L;
	
	/** 数据标识 */
    @Excel(name = "编号")
	private String id;

	/** 姓名 */
    @Excel(name = "名称")
	private String name;

    /** 报告人 */
    @Excel(name = "报告人")
    private int reporter;

    /** 部门 */
    @Excel(name = "部门")
    private String depts;


    /** 文件路径 */
    @Excel(name = "文件路径")
    private String filePath;

    /** 图片路径 */
    @Excel(name = "图片路径")
    private String imgPath;

    /** 用户1 */
    @Excel(name = "用户1")
    private String userids1;

    /** 用户2 */
    @Excel(name = "用户2")
    private String userids2;

    @TableField(exist = false)
    private String creator;

    public String getCreator() {
        return creator+"（来源于第二张表）";
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }


    public String getUserids1() {
        return userids1;
    }

    public void setUserids1(String userids1) {
        this.userids1 = userids1;
    }

    public String getUserids2() {
        return userids2;
    }

    public void setUserids2(String userids2) {
        this.userids2 = userids2;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getReporter() {
        return reporter;
    }

    public void setReporter(int reporter) {
        this.reporter = reporter;
    }

    public String getDepts() {
        return depts;
    }

    public void setDepts(String depts) {
        this.depts = depts;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public void setId(String id)
	{
		this.id = id;
	}

	public String getId() 
	{
		return id;
	}
	public void setName(String name) 
	{
		this.name = name;
	}

	public String getName() 
	{
		return name;
	}

	@Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .toString();
    }
}
