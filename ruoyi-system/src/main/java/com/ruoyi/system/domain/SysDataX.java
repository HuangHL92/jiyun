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
 * Datax配置表 dson_job_in
 *
 * @author jiyunsoft
 * @date 2019-04-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_datax_json")
public class SysDataX extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final Integer DATAX_SCHEDULE_YES = 1;
    public static final Integer DATAX_SCHEDULE_NO = 0;
    /**
     * 主键
     */
    @TableId
    private String id;
    /**
     * 创建时间
     */
    @TableField(exist = false)
    private Date createDate;
    /**
     * 更新时间
     */
    @TableField(exist = false)
    private Date updateDate;
    /**
     * 备注信息
     */
    private String remarks;
    /**
     * 生产数据库主键
     */
    private String splitPk;
    /**
     * 生产列名
     */
    private String readerColumn;
    /**
     * 生产表名
     */
    private String readerTableName;
    /**
     * 生产库连接
     */
    private String readerPort;
    /**
     * 生产库用户名
     */
    private String readerUserName;
    /**
     * 生产库密码
     */
    private String readerPassword;
    /**
     * 生产库增量sql
     */
    private String readerWhere;
    /**
     * 写入表名
     */
    private String writerTableName;
    /**
     * 写入表链接
     */
    private String writerPort;
    /**
     * 执行前SQL
     */
    private String perSql;
    /**
     * 执行模式
     */
    private String writerMode;
    /**
     * 同步表用户名
     */
    private String writerUserName;
    /**
     * 同步表密码
     */
    private String writerPassword;
    /**
     * 配置文件名
     */
    private String fileName;
    /**
     * 输出日志
     */
    private String log;
    /**
     * 最终执行时间
     */
    private Date finalDo;
    /**
     * 下次执行时间
     */
    private Date nextDo;
    /**
     * 定时任务名
     */
    private String scheduleName;
    /**
     * 定时表达式
     */
    private String scheduleCron;
    /**
     * 定时任务ID
     */
    private String scheduleId;
    /**
     * 0无1有
     */
    private Integer isSchedule;
    /**
     * 数据库类型 (枚举类 ：DataXSqlType) 1 mysql  2 oracle 3 sqlserver
     */
    private String sqlType;
    /**
     * 数据库类型 (枚举类 ：DataXSqlType) 1 mysql  2 oracle 3 sqlserver
     */
    private String sqlTypeWriter;
    @TableField(exist = false)
    private String speedByteName = "byte";
    @TableField(exist = false)
    private Integer speed = 1048576;
    @TableField(exist = false)
    private String channelName = "channel";
    @TableField(exist = false)
    private Integer channel = 5;
    @TableField(exist = false)
    private String oldFileName;
    @TableField(exist = false)
    private String keyword;
}
