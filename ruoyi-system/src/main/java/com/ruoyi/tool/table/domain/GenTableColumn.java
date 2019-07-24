package com.ruoyi.tool.table.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.base.BaseEntity;
import com.ruoyi.common.utils.StringUtils;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("gen_table_column")
public class GenTableColumn extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public static final String DEL_FLAG_NORMAL = "0"; // 未删除
    public static final String DEL_FLAG_DELETE = "1"; // 已删除
    public static final String YES = "1"; // 对
    public static final String NO = "0"; // 错
    public static final String PK_NAME = "id";  // 主键名称


    @TableId(type = IdType.UUID)
    private String id;

    private String genTableId;

    private String name;

    private String oldName;

    private String comments;

    private String oldComments;

    private String jdbcType;

    private String listshow;

    @TableField(exist = false)
    private String viewJdbc; // 合成jdbcType的作用

    @TableField(exist = false)
    private String length; // 合成jdbcType的作用

    private String oldJdbcType;

    private String isPk; //数据库默认为0

    private String isNull; // 数据库默认0

    private String oldIsPk;

    private Integer orderNum;

    @TableField(exist = false)
    private GenTable genTable;

    public GenTableColumn(){

    }

    public GenTableColumn(String name, String comments, String jdbcType) {
        this(name, comments, jdbcType, "0");
    }

    public GenTableColumn(String name, String comments, String jdbcType, String isPk) {
        this.name = name;
        this.comments = comments;
        this.jdbcType = jdbcType;
        this.setIsPk(isPk);
        this.setDelFlag("0");
    }

    public GenTableColumn(GenTable genTable) {
        this.genTable = genTable;
    }


    public String getViewJdbc() {
        if (this.viewJdbc == null) {
            if (this.getJdbcType().indexOf("(") > -1) {
                return this.getJdbcType().substring(0, this.getJdbcType().indexOf("("));
            } else {
                return this.getJdbcType();
            }
        }
        return this.viewJdbc;
    }

    public void setViewJdbc(String viewJdbc) {
        this.viewJdbc = viewJdbc;
    }

    public String getLength() {
        if (this.length == null ) {
            if (this.getJdbcType().indexOf("(") > -1) {
                return this.getJdbcType().substring(this.getJdbcType().indexOf("(") + 1, this.getJdbcType().indexOf(")"));
            } else {
                return "";
            }
        }
        return this.length;
    }

    public Long getLengthNumber() {
        String length = getLength();
        if (StringUtils.isNotEmpty(length)) {
            if (length.indexOf(",") > -1) {
                return Long.parseLong(length.substring(0, length.indexOf(",")));
            }
            return Long.parseLong(length);
        }
        return null;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getJdbcType() {
        if (this.jdbcType == null) {
            if ("TEXT".equals(StringUtils.upperCase(this.getViewJdbc()))
                    || "LONGTEXT".equals(StringUtils.upperCase(this.getViewJdbc()))
                    || "LONGBLOB".equals(StringUtils.upperCase(this.getViewJdbc()))
                    || "DATETIME".equals(StringUtils.upperCase(this.getViewJdbc()))
                    || "TIMESTAMP".equals(StringUtils.upperCase(this.getViewJdbc()))) {
                this.jdbcType = this.getViewJdbc();
            } else {
                this.jdbcType = this.getViewJdbc() + "(" + this.getLength() + ")";
            }
        }
        return this.jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getNameAndComments() {
        return this.getName() + (this.comments == null ? "" : "  :  " + this.comments);
    }

    public String getDataLength() {
        String[] ss = StringUtils.split(StringUtils.substringBetween(this.getJdbcType(), "(", ")"), ",");
        return ss != null && ss.length == 1 ? ss[0] : "0";
    }

}
