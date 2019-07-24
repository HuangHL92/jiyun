package com.ruoyi.system.domain.SysDataXType;


import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * (复杂json文件生成实体类)
 * DataX Key类(满足日常需要)
 * 需安装lombok插件或自行添加get,set方法
 *
 * @Author: Zen
 * @Date: 2018/10/29 16:23
 */
@Data
public class SysDataXInfo {


    private Job job;

    @Data
    public static class Job {
        public static Object Content;
        private Setting setting;
        private List<Content> content;

        @Data
        public static class Setting {
            private Map<String, Object> speed;
            private ErrorLimit errorLimit;


            @Data
            public static class ErrorLimit {
                private Integer record = 0;
                private Double percentage = 0.02;
            }
        }

        @Data
        public static class Content {
            private Reader reader;
            private Writer writer;

            @Data
            public static class Reader {
                private String name = "mysqlreader";
                private Parameter parameter;

                @Data
                public static class Parameter {
                    private String username;
                    private String password;
                    private List<String> column;
                    private String splitPk;
                    private List<Connection> connection;
                    private String where;

                    @Data
                    public static class Connection {
                        private List<String> table;
                        private List<String> jdbcUrl;
                    }

                }


            }

            @Data
            public static class Writer {
                private String name = "mysqlwriter";
                private Parameter parameter;

                @Data
                public static class Parameter {
                    private String writeMode;
                    private String username;
                    private String password;
                    private List<String> column;
                    private List<String> preSql;
                    private List<Connection> connection;

                    @Data
                    public static class Connection {
                        private List<String> table;
                        private String jdbcUrl;
                    }

                }
            }

        }

    }


}
