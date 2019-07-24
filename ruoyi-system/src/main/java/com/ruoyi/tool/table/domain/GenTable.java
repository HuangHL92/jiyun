package com.ruoyi.tool.table.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

import com.ruoyi.common.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("gen_table")
public class GenTable extends BaseEntity {
    private static final long serialVersionUID = 1L;

    public static final String SYNCHED = "1";
    public static final String UN_SYNCHED = "0";
    public static final String IDTYPE_UUID = "UUID";
    public static final String IDTYPE_AUTO = "AUTO";

    @TableId(type = IdType.UUID)
    private String id;

    private String name;

    private String oldName;

    private String comments;

    private String oldComments;

    private String isSync;

    private String genIdType = IDTYPE_UUID; // 默认生成策略UUID

    private String oldGenIdType;

    // 不包含删除的column
    @TableField(exist = false)
    private List<GenTableColumn> columnList = Lists.newArrayList();
    // 包含所有column
    @TableField(exist = false)
    private List<GenTableColumn> allColumnList = Lists.newArrayList();
    @TableField(exist = false)
    private List<String> pkList;

    /**
     * 表总记录数
     */
    @TableField(exist = false)
    private Integer totalCount;

    public GenTable() {
    }

    public String getNameAndComments() {
        return this.getName() + (this.comments == null ? "" : "  :  " + this.comments);
    }

    public Boolean getCreateDateExists() {
        Iterator iterator = this.columnList.iterator();

        while (iterator.hasNext()) {
            GenTableColumn column = (GenTableColumn) iterator.next();
            if ("create_time".equals(column.getName())) {
                return true;
            }
        }

        return false;
    }

    public Boolean getUpdateDateExists() {
        Iterator iterator = this.columnList.iterator();

        while (iterator.hasNext()) {
            GenTableColumn column = (GenTableColumn) iterator.next();
            if ("update_time".equals(column.getName())) {
                return true;
            }
        }

        return false;
    }

    public Boolean getDelFlagExists() {
        Iterator iterator = this.columnList.iterator();

        while (iterator.hasNext()) {
            GenTableColumn column = (GenTableColumn) iterator.next();
            if ("del_flag".equals(column.getName())) {
                return true;
            }
        }

        return false;
    }

}
