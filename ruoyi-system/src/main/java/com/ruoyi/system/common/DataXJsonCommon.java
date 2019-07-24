package com.ruoyi.system.common;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;

import com.ruoyi.common.config.Global;
import com.ruoyi.common.enums.DataXSqlType;
import com.ruoyi.system.domain.SysDataX;
import com.ruoyi.system.domain.SysDataXType.SysDataXInfo;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;


/**
 * dataX工具类
 *
 * @Author: Zen
 * @Date: 2018/10/30 15:32
 */
public class DataXJsonCommon {
    /**
     * json文件夹路径
     */
    public static String JSON_PATH = Global.getJsonDataXPath();
    /**
     * datax的python文件地址
     */
    public static String DATAX_PATH = Global.getDataXExePath();
    /**
     * dataX log文件夹路径
     */
    public static String LOG_PATH = Global.getJsonDataXLogPath();

    /**
     * 删除dataXjson文件和log日志
     *
     * @param fileName
     */
    public static void delJsonAndLog(String fileName) {
        String jsonFilePath = JSON_PATH + fileName + ".json";
        String logFilePath = LOG_PATH + fileName + "_json.log";
        FileUtil.del(jsonFilePath);
        FileUtil.del(logFilePath);
    }

    /**
     * 读取Datax log内容
     *
     * @param fileName
     * @return
     * @throws IOException
     */

    public static String readToString(String fileName) {
        try {
            String filePath = LOG_PATH + fileName + "_json.log";
            File file = new File(filePath);
            // 获取文件长度
            Long fileLength = file.length();
            byte[] fileContent = new byte[fileLength.intValue()];

            FileInputStream in = new FileInputStream(file);
            in.read(fileContent);
            in.close();
            String fileContentS = new String(fileContent);
            // 返回文件内容,默认编码
            return fileContentS;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 执行dataX作业
     *
     * @param fileName
     */
    public static String exeDataX(String fileName) {
        try {
            String line;
            String filePath = LOG_PATH + fileName + "_json.log";
//            File file = new File(filePath);
            //文件不存在则创建文件，先创建目录
            if (FileUtil.exist(filePath)) {
                //存在删除文件
                FileUtil.del(JSON_PATH + fileName+ "_json.log");
            }
            //创建新的log文件
            File file = FileUtil.touch(filePath);
            //文件输出流用于将数据写入文件
            FileOutputStream outStream = new FileOutputStream(file);
            System.out.println("------------------start----------------------");
            /**
             * 执行Python命令
             */
            String windowCmd = "python" + " " + DATAX_PATH + "datax.py" + " " + JSON_PATH + fileName + ".json";
            Process pr = Runtime.getRuntime().exec(windowCmd);
            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            while ((line = in.readLine()) != null) {
                outStream.write(line.getBytes());
                outStream.write("\n".getBytes());
                /**
                 * 打印输出日志
                 */
                System.out.println(line);
            }
            in.close();
            outStream.close();
            pr.waitFor();
            System.out.println("----------------end------------------");
            String log = readToString(fileName);
            return log;
        } catch (Exception e) {
            return e.getMessage();
        }

    }

    /**
     * 批量执行dataX作业
     */
    public static void exeDataxs() {
        try {
            StringBuilder logStr = new StringBuilder();
            System.out.println("------------------start----------------------");
            String[] str = getFileName(JSON_PATH);
            for (String name : str) {
                String windowcmd = "python" + " " + DATAX_PATH + "datax.py" + " " + JSON_PATH + "/" + name + ".json";
                System.out.println(windowcmd);
                Process pr = Runtime.getRuntime().exec(windowcmd);
                BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }
                in.close();
                pr.waitFor();
            }
            System.out.println("----------------end------------------");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 获取文件夹下所有 json 文件名
     *
     * @param path
     * @return
     */
    public static String[] getFileName(String path) {
        File file = new File(path);
        String[] fileName = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith(".json")) {
                    return true;
                }
                return false;
            }
        });
        return fileName;
    }

    public static Connection getConnection(String sqlType,String url, String name, String pwd) {
        try {
            if(String.valueOf(DataXSqlType.MYSQL.getType()).equals(sqlType)){
                Class.forName(DataXSqlType.MYSQL.getDriver());
            }else if(String.valueOf(DataXSqlType.ORACLE.getType()).equals(sqlType)){
                Class.forName(DataXSqlType.ORACLE.getDriver());
            }else if(String.valueOf(DataXSqlType.SQL_SERVER.getType()).equals(sqlType)){
                Class.forName(DataXSqlType.SQL_SERVER.getDriver());
            }
            //获取连接对象 链接 用户名 密码
            Connection conn = DriverManager.getConnection(url, name, pwd);
            conn.close();
            return conn;
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException:" + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.out.println("SQLException:" + e.getMessage());
            return null;
        }


    }


    /**
     * 生成dataX做业基础模板只支持Mysql
     *
     * @param sysDataX
     */
    public static Boolean dataxJsonMod(SysDataX sysDataX) {
        //准备数据 开始
        String fileNames = sysDataX.getFileName() + ".json";
        String readerPort = null;
        String writerPort = null;
        if (String.valueOf(DataXSqlType.MYSQL.getType()).equals(sysDataX.getSqlType())) {
            readerPort = DataXSqlType.MYSQL.getPrefix() + sysDataX.getReaderPort() + DataXSqlType.MYSQL.getSuffix();
            writerPort = DataXSqlType.MYSQL.getPrefix() + sysDataX.getWriterPort() + DataXSqlType.MYSQL.getSuffix();
        } else if (String.valueOf(DataXSqlType.ORACLE.getType()).equals(sysDataX.getSqlType())) {
            readerPort = DataXSqlType.ORACLE.getPrefix() + sysDataX.getReaderPort() + DataXSqlType.ORACLE.getSuffix();
            writerPort = DataXSqlType.ORACLE.getPrefix() + sysDataX.getWriterPort() + DataXSqlType.ORACLE.getSuffix();
        } else if (String.valueOf(DataXSqlType.SQL_SERVER.getType()).equals(sysDataX.getSqlType())) {
            readerPort = DataXSqlType.SQL_SERVER.getPrefix() + sysDataX.getReaderPort() + DataXSqlType.SQL_SERVER.getSuffix();
            writerPort = DataXSqlType.SQL_SERVER.getPrefix() + sysDataX.getWriterPort() + DataXSqlType.SQL_SERVER.getSuffix();
        }

        String cols = sysDataX.getReaderColumn().replace('，', ',');
        String[] columns = cols.split(",");
        List<String> column = Arrays.asList(columns);
        List<SysDataXInfo.Job.Content.Reader.Parameter.Connection> readerConnectionList = new ArrayList<>();
        SysDataXInfo.Job.Content.Reader.Parameter.Connection readerConnection = new SysDataXInfo.Job.Content.Reader.Parameter.Connection();
        List<String> readerTable = new ArrayList<>();
        //pojo
        readerTable.add(sysDataX.getReaderTableName());
        List<String> readerJdbcUrl = new ArrayList<>();
        readerConnectionList.add(readerConnection);
        //pojo
        readerJdbcUrl.add(readerPort);
        readerConnection.setTable(readerTable);
        readerConnection.setJdbcUrl(readerJdbcUrl);


        SysDataXInfo.Job.Content.Reader.Parameter readerParameter = new SysDataXInfo.Job.Content.Reader.Parameter();
        //pojo
        readerParameter.setUsername(sysDataX.getReaderUserName());
        //pojo
        readerParameter.setPassword(sysDataX.getReaderPassword());
        readerParameter.setColumn(column);
        //pojo
        readerParameter.setSplitPk(sysDataX.getSplitPk());
        readerParameter.setConnection(readerConnectionList);
        //pojo
        readerParameter.setWhere(sysDataX.getReaderWhere());


        List<SysDataXInfo.Job.Content.Writer.Parameter.Connection> writerConnectionList = new ArrayList<>();
        SysDataXInfo.Job.Content.Writer.Parameter.Connection writerConnection = new SysDataXInfo.Job.Content.Writer.Parameter.Connection();
        List<String> writerTable = new ArrayList<>();
        //pojo
        writerTable.add(sysDataX.getReaderTableName());
        writerConnectionList.add(writerConnection);
        writerConnection.setTable(writerTable);
        writerConnection.setJdbcUrl(writerPort);

        SysDataXInfo.Job.Content.Writer.Parameter writerParameter = new SysDataXInfo.Job.Content.Writer.Parameter();
        List<String> writerPreSql = new ArrayList<>();
        //pojo
        writerPreSql.add(sysDataX.getPerSql());
        //pojo
        writerParameter.setWriteMode(sysDataX.getWriterMode());
        //pojo
        writerParameter.setUsername(sysDataX.getWriterUserName());
        //pojo
        writerParameter.setPassword(sysDataX.getWriterPassword());
        writerParameter.setColumn(column);
        writerParameter.setPreSql(writerPreSql);
        writerParameter.setConnection(writerConnectionList);

        SysDataXInfo.Job.Content.Reader reader = new SysDataXInfo.Job.Content.Reader();
        reader.setParameter(readerParameter);
        if(String.valueOf(DataXSqlType.MYSQL.getType()).equals(sysDataX.getSqlType())){
            reader.setName(DataXSqlType.MYSQL.getSqlReaderDriver());
        }else if(String.valueOf(DataXSqlType.ORACLE.getType()).equals(sysDataX.getSqlType())){
            reader.setName(DataXSqlType.ORACLE.getSqlReaderDriver());
        }else if(String.valueOf(DataXSqlType.SQL_SERVER.getType()).equals(sysDataX.getSqlType())){
            reader.setName(DataXSqlType.SQL_SERVER.getSqlReaderDriver());
        }

        SysDataXInfo.Job.Content.Writer writer = new SysDataXInfo.Job.Content.Writer();
        if(String.valueOf(DataXSqlType.MYSQL.getType()).equals(sysDataX.getSqlTypeWriter())){
            writer.setName(DataXSqlType.MYSQL.getSqlWriterDriver());
        }else if(String.valueOf(DataXSqlType.ORACLE.getType()).equals(sysDataX.getSqlTypeWriter())){
            writer.setName(DataXSqlType.ORACLE.getSqlWriterDriver());
        }else if(String.valueOf(DataXSqlType.SQL_SERVER.getType()).equals(sysDataX.getSqlTypeWriter())){
            writer.setName(DataXSqlType.SQL_SERVER.getSqlWriterDriver());
        }
        writer.setParameter(writerParameter);

        SysDataXInfo.Job.Content content = new SysDataXInfo.Job.Content();
        content.setReader(reader);
        content.setWriter(writer);

        Map<String, Object> speedMap = new HashMap<>();
        speedMap.put(sysDataX.getSpeedByteName(), sysDataX.getSpeed());
        speedMap.put(sysDataX.getChannelName(), sysDataX.getChannel());
        //errorLimit
        SysDataXInfo.Job.Setting.ErrorLimit errorLimit = new SysDataXInfo.Job.Setting.ErrorLimit();
        //errorLimit
        SysDataXInfo.Job.Setting setting = new SysDataXInfo.Job.Setting();
        setting.setSpeed(speedMap);
        setting.setErrorLimit(errorLimit);

        List<SysDataXInfo.Job.Content> contentList = new ArrayList<>();
        contentList.add(content);
        //准备数据 结束
        //判断数据库链接正确
//        if (getConnection(readerPort, readerParameter.getUsername(), readerParameter.getPassword()) != null
//                && getConnection(writerPort, writerParameter.getUsername(), writerParameter.getPassword()) != null) {
            //job
            SysDataXInfo.Job job = new SysDataXInfo.Job();
            job.setSetting(setting);
            job.setContent(contentList);
            SysDataXInfo sysDataXInfo = new SysDataXInfo();
            sysDataXInfo.setJob(job);

            createJson(sysDataXInfo, fileNames);
            //连接正常
//            return true;
//        } else {
//            //链接错误
//            return false;
//        }

        return true;
    }

    /**
     * 创建文件
     *
     * @param object   对象
     * @param jsonFile 文件名
     */
    public static void createJson(Object object, String jsonFile) {
        //将对象格式化 json
        String jsonPrettyStr = JSONUtil.toJsonPrettyStr(object);
        // 文件夹路径
        if (FileUtil.exist(JSON_PATH + jsonFile)) {
            //存在删除文件
            FileUtil.del(JSON_PATH + jsonFile);
        }
        //创建文件重新写入
        FileUtil.writeString(jsonPrettyStr, (JSON_PATH + jsonFile), "UTF-8");
    }

}
