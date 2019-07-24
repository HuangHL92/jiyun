package com.ruoyi.system.domain.SysDataXType;

import lombok.Data;

/**
 * @Author: Zen
 * @Date: 2018/10/30 15:29
 */
@Data
public class SysDataXBase {

    private String splitPk;//"id"
    private String readerColumn;// "*"
    private String readerTableName; // "t_house"
    // "jdbc:mysql://192.172.18.222:3306/jy_wlrkgl?characterEncoding=utf8"
    private String readerPort = this.getReaderPort();
    private String readerUserName;//"jiyun"
    private String readerPassword;//"****"

    private String writerTableName; // "t_house"
    // "jdbc:mysql://localhost:3306/jy_wlrkgl?characterEncoding=utf8"
    private String writerPort = this.getWriterPort();
    private String perSql; //"delete from t_house"
    private String writerMode;//"insert"
    private String writerUserName;//"root"
    private String writerPassword;//"****"
    private String speedByteName = "byte";
    private Integer speed = 1048576;
    private String channelName = "channel";
    private Integer channel = 5;
    private String fileName;
    private String log;
}
