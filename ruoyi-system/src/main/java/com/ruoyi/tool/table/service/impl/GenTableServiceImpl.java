package com.ruoyi.tool.table.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.tool.table.domain.GenTable;
import com.ruoyi.tool.table.domain.GenTableColumn;
import com.ruoyi.tool.table.mapper.GenTableColumnMapper;
import com.ruoyi.tool.table.mapper.GenTableMapper;
import com.ruoyi.tool.table.service.IGenTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: xjm
 * @date: 2019/04/01
 */
@Service
public class GenTableServiceImpl extends ServiceImpl<GenTableMapper, GenTable> implements IGenTableService {

    private static final Logger log = LoggerFactory.getLogger(GenTableServiceImpl.class);

    @Autowired
    private GenTableColumnMapper genTableColumnMapper;
    @Autowired
    private GenTableMapper genTableMapper;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<GenTable> selectAllList(GenTable genTable) {
        QueryWrapper<GenTable> query = new QueryWrapper<>();
        query.lambda().and(StrUtil.isNotBlank(genTable.getComments()),
                i -> i.like(GenTable::getComments, genTable.getComments()));
        query.lambda().and(StrUtil.isNotBlank(genTable.getName()),
                i -> i.like(GenTable::getName, genTable.getName()));
        return genTableMapper.selectListWithCount(query);
    }

    @Override
    @Transactional(readOnly = true)
    public GenTable selectTableById(String genTableId) {
        QueryWrapper<GenTable> query = new QueryWrapper<>();
        QueryWrapper<GenTableColumn> columnQuery = new QueryWrapper<>();
        query.lambda().eq(GenTable::getId, genTableId);
        GenTable genTable = getOne(query);
        columnQuery.lambda().eq(GenTableColumn::getGenTableId, genTable.getId());
        columnQuery.lambda().orderByAsc(GenTableColumn::getOrderNum);
        genTable.setAllColumnList(genTableColumnMapper.selectList(columnQuery));
        columnQuery.lambda().eq(GenTableColumn::getDelFlag, GenTableColumn.DEL_FLAG_NORMAL);
        genTable.setColumnList(genTableColumnMapper.selectList(columnQuery));
        return genTable;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertTable(GenTable genTable) {
        if (checkIsTableExist(genTable))
            throw new BusinessException("表单已存在");
        genTable.setIsSync(GenTable.UN_SYNCHED);
        boolean result = save(genTable);
        List<GenTableColumn> columnList = genTable.getColumnList();
        for (GenTableColumn column : columnList) {
            column.setGenTableId(genTable.getId());
            column.setDelFlag(GenTableColumn.DEL_FLAG_NORMAL);
            if (genTableColumnMapper.insert(column) <= 0) {
                throw new BusinessException("插入表单字段失败");
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTable(GenTable genTable) {
        UpdateWrapper<GenTable> updater = new UpdateWrapper<>();
        List<GenTableColumn> columnList = genTable.getColumnList();

        // 原记录
        GenTable old = this.selectTableById(genTable.getId());
        List<GenTableColumn> oldColumns = old.getAllColumnList(); // 全部column

        // 验证表单是否存在
        if (checkIsTableExist(genTable))
            throw new BusinessException("表单已存在");

        // 验证是否有字段变更
        if (checkNeedSynch(genTable,old)) {
            updater.lambda().set(GenTable::getIsSync, GenTable.UN_SYNCHED); // 有字段变更把同步flag设为未同步
        }

        for (GenTableColumn oldColumn : oldColumns) {
            boolean isMatched = false;
            for (GenTableColumn newColumn : columnList) {
                if (oldColumn.getId().equals(newColumn.getId())) {
                    isMatched = true;
                    oldColumn.setOrderNum(newColumn.getOrderNum()); // 匹配到就更新排序
                    oldColumn.setJdbcType(newColumn.getJdbcType());
                    oldColumn.setName(newColumn.getName());
                    oldColumn.setComments(newColumn.getComments());
                    oldColumn.setIsPk(newColumn.getIsPk());
                    oldColumn.setListshow(newColumn.getListshow());
                    // 必须要之前同步过的table才能设置old*
                    if (!oldColumn.getName().equals(newColumn.getName())
                            && GenTable.SYNCHED.equals(genTable.getIsSync())) {
                        oldColumn.setOldName(oldColumn.getName()); // 先记录old，然后再赋值new
                    }
                    if (!oldColumn.getComments().equals(newColumn.getComments())
                            && GenTable.SYNCHED.equals(genTable.getIsSync())) {
                        oldColumn.setOldComments(oldColumn.getComments());
                    }
                    if (!oldColumn.getIsPk().equals(newColumn.getIsPk())
                            && GenTable.SYNCHED.equals(genTable.getIsSync())) {
                        oldColumn.setOldIsPk(oldColumn.getIsPk());
                    }
                    if (!oldColumn.getJdbcType().equals(newColumn.getJdbcType())
                            && GenTable.SYNCHED.equals(genTable.getIsSync())) {
                        oldColumn.setOldJdbcType(oldColumn.getJdbcType());
                    }
                }
            }
            if (!isMatched) {
                oldColumn.setDelFlag(GenTableColumn.DEL_FLAG_DELETE); // 没匹配到删除
            }
            genTableColumnMapper.updateById(oldColumn);
        }

        for (GenTableColumn newColumn : columnList) {
            boolean isMatched = false;
            for (GenTableColumn oldColumn : oldColumns) {
                if (oldColumn.getId().equals(newColumn.getId())) {
                    isMatched = true;
                }
            }
            if (!isMatched) {
                newColumn.setGenTableId(old.getId());
                genTableColumnMapper.insert(newColumn); // 没匹配到新增
            }
        }

        // 更新gen_table字段
        updater.lambda().set(GenTable::getName, genTable.getName());
        updater.lambda().set(GenTable::getComments, genTable.getComments());
        updater.lambda().set(GenTable::getGenIdType, genTable.getGenIdType());
        // 必须要之前同步过的table才能设置old*
        if (!old.getName().equals(genTable.getName())
                && GenTable.SYNCHED.equals(genTable.getIsSync())) {
            updater.lambda().set(GenTable::getOldName, old.getName());
        }
        if (!old.getComments().equals(genTable.getComments())
                && GenTable.SYNCHED.equals(genTable.getIsSync())) {
            updater.lambda().set(GenTable::getOldComments, old.getComments());
        }
        if (!old.getGenIdType().equals(genTable.getGenIdType())
                && GenTable.SYNCHED.equals(genTable.getIsSync())) {
            updater.lambda().set(GenTable::getOldGenIdType, old.getGenIdType());
        }
        updater.lambda().eq(GenTable::getId, old.getId());

        return update(updater);
    }


    @Override
    public int buildTable(String sql) {
        GenTableMapper genTableMapper = getBaseMapper();
        return genTableMapper.buildTable(sql);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTable(String ids) {
        List<String> idList = CollUtil.newArrayList(ids.split(","));
        // 删除对应的column表
        idList.forEach(id -> {
            UpdateWrapper<GenTableColumn> updater = new UpdateWrapper<>();
            updater.lambda().eq(GenTableColumn::getGenTableId, id);
            genTableColumnMapper.delete(updater);
        });
        removeByIds(idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTable(String ids) {
        List<String> idList = CollUtil.newArrayList(ids.split(","));
        // 删除对应的column表
        idList.forEach(id -> {
            GenTable genTable = this.getBaseMapper().selectById(id);
            StringBuffer sb = new StringBuffer();
            sb.append("drop table if exists " + genTable.getName() + " ;");
            this.buildTable(sb.toString()); // 物理删除表
            UpdateWrapper<GenTableColumn> updater = new UpdateWrapper<>();
            updater.lambda().eq(GenTableColumn::getGenTableId, id);
            genTableColumnMapper.delete(updater); // 删除gen_table_column表数据
        });
        removeByIds(idList);
    }

    /**
     * 同步数据库（DDL）
     * @param id
     * @param isForce
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synchDb(String id, boolean isForce) {

        GenTable orgTable = this.selectTableById(id);
        List<GenTableColumn> columnList = orgTable.getColumnList();
        List<GenTableColumn> allColumnList = orgTable.getAllColumnList();
        UpdateWrapper<GenTable> tableUpdater;
        UpdateWrapper<GenTableColumn> columnUpdater;
        StringBuffer sb;
        String columnName;
        String oldColumnName;
        String newColumnName;
        String jdbcType;
        String pk;
        Iterator iterator;
        GenTableColumn genTableColumn;

        try {

            // 普通同步并且不是新表
            if (!isForce && orgTable.getOldName() != null) {
                sb = new StringBuffer();
                if (!orgTable.getName().equalsIgnoreCase(orgTable.getOldName())) {
                    sb.append("ALTER  TABLE " + orgTable.getOldName() + " RENAME TO " + orgTable.getName() + ";");
                    this.buildTable("ALTER  TABLE " + orgTable.getOldName() + " RENAME TO " + orgTable.getName() + ";");
                    tableUpdater = new UpdateWrapper<>();
                    tableUpdater.lambda().set(GenTable::getOldName, orgTable.getName());
                    tableUpdater.lambda().eq(GenTable::getId, orgTable.getId());
                    update(tableUpdater);
                }
                if (!orgTable.getComments().equals(orgTable.getOldComments())) {
                    sb.append("alter table " + orgTable.getName() + " comment '" + orgTable.getComments() + "';");
                    this.buildTable("alter table " + orgTable.getName() + " comment '" + orgTable.getComments() + "';");
                    tableUpdater = new UpdateWrapper<>();
                    tableUpdater.lambda().set(GenTable::getOldComments, orgTable.getComments());
                    update(tableUpdater);
                }

                // 判断是否有字段需要删除
                iterator = allColumnList.iterator();
                while (iterator.hasNext()) {
                    genTableColumn = (GenTableColumn) iterator.next();
                    oldColumnName = genTableColumn.getOldName();
                    if (genTableColumn.getDelFlag().equals(GenTableColumn.DEL_FLAG_DELETE)
                            && this.isColumnExistInTable(orgTable.getName(), oldColumnName)) {
                        sb.append("alter table " + orgTable.getName() + " drop " + oldColumnName + ";");
                        this.buildTable("alter table " + orgTable.getName() + " drop " + oldColumnName + ";");
                        columnUpdater = new UpdateWrapper<>();
                        columnUpdater.lambda().eq(GenTableColumn::getId, genTableColumn.getId());
                        genTableColumnMapper.delete(columnUpdater); // 同步完成后把标记为已删除的字段物理删除
                    }
                }

                iterator = allColumnList.iterator();

                mark:
                while (true) {
                    do {
                        do {
                            do {
                                if (!iterator.hasNext()) { // 是否是最后一条
                                    iterator = allColumnList.iterator();

                                    while (iterator.hasNext()) {
                                        genTableColumn = (GenTableColumn) iterator.next();
                                        oldColumnName = genTableColumn.getOldName();
                                        newColumnName = genTableColumn.getName();
                                        if (!genTableColumn.getDelFlag().equals(GenTableColumn.DEL_FLAG_DELETE) && oldColumnName == null) {
                                            sb.append("alter table " + orgTable.getName() + " add " + newColumnName + " " + genTableColumn.getJdbcType() + " comment '" + genTableColumn.getComments() + "';");
                                            this.buildTable("alter table " + orgTable.getName() + " add " + newColumnName + " " + genTableColumn.getJdbcType() + " comment '" + genTableColumn.getComments() + "';");
                                            columnUpdater = new UpdateWrapper<>();
                                            columnUpdater.lambda().eq(GenTableColumn::getId, genTableColumn.getId());
                                            columnUpdater.lambda().set(GenTableColumn::getOldName, genTableColumn.getName());
                                            columnUpdater.lambda().set(GenTableColumn::getOldComments, genTableColumn.getComments());
                                            columnUpdater.lambda().set(GenTableColumn::getOldIsPk, genTableColumn.getIsPk());
                                            columnUpdater.lambda().set(GenTableColumn::getOldJdbcType, genTableColumn.getJdbcType());
                                            genTableColumnMapper.update(null, columnUpdater);
                                        }
                                    }

                                    if (orgTable.getGenIdType() != null && !orgTable.getGenIdType().equals(orgTable.getOldGenIdType())) {
                                        iterator = allColumnList.iterator();
                                        while (iterator.hasNext()) {
                                            genTableColumn = (GenTableColumn) iterator.next();
                                            if (!genTableColumn.getDelFlag().equals(GenTableColumn.DEL_FLAG_DELETE) && genTableColumn.getName() != null && genTableColumn.getIsPk().equals(GenTableColumn.YES)) {
                                                if (orgTable.getGenIdType().equals(GenTable.IDTYPE_AUTO)) {
                                                    jdbcType = genTableColumn.getJdbcType();
                                                    if (!jdbcType.toLowerCase().contains("int") && !jdbcType.toLowerCase().contains("integer")) {
                                                        jdbcType = "integer";
                                                        columnUpdater = new UpdateWrapper<>();
                                                        columnUpdater.lambda().set(GenTableColumn::getJdbcType, jdbcType);
                                                        columnUpdater.lambda().set(GenTableColumn::getOldJdbcType, jdbcType);
                                                        columnUpdater.lambda().eq(GenTableColumn::getId, genTableColumn.getId());
                                                        genTableColumnMapper.update(null, columnUpdater);
                                                    }
                                                    this.buildTable("alter table " + orgTable.getName() + " change   " + genTableColumn.getName() + " " + genTableColumn.getName() + " " + jdbcType + " auto_increment ;");
                                                }
                                            } else {
                                                jdbcType = genTableColumn.getJdbcType();
                                                if (!jdbcType.toLowerCase().contains("varchar")) {
                                                    jdbcType = "varchar(64)";
                                                    genTableColumn.setJdbcType(jdbcType);
                                                    columnUpdater = new UpdateWrapper<>();
                                                    columnUpdater.lambda().set(GenTableColumn::getJdbcType, jdbcType);
                                                    columnUpdater.lambda().set(GenTableColumn::getOldJdbcType, jdbcType);
                                                    columnUpdater.lambda().eq(GenTableColumn::getId, genTableColumn.getId());
                                                    genTableColumnMapper.update(null, columnUpdater);
                                                }
                                                this.buildTable("alter table " + orgTable.getName() + " change   " + genTableColumn.getName() + " " + genTableColumn.getName() + " " + jdbcType + " ;");
                                            }
                                        }
                                    }

                                    jdbcType = "";
                                    pk = "";
                                    iterator = columnList.iterator();

                                    while (iterator.hasNext()) {
                                        genTableColumn = (GenTableColumn) iterator.next();
                                        if (genTableColumn.getIsPk().equals(GenTableColumn.YES)) {
                                            jdbcType = jdbcType + genTableColumn.getName() + ",";
                                        }

                                        if (GenTableColumn.YES.equals(genTableColumn.getOldIsPk())) {
                                            pk = pk + genTableColumn.getName() + ",";
                                        }
                                    }

                                    if (!pk.equals(jdbcType)) {
                                        sb.append("alter table " + orgTable.getName() + " drop primary key;");
                                        this.buildTable("alter table " + orgTable.getName() + " drop primary key;");
                                        iterator = columnList.iterator();

                                        while (iterator.hasNext()) {
                                            genTableColumn = (GenTableColumn) iterator.next();
                                            if (GenTableColumn.YES.equals(genTableColumn.getOldIsPk())) {
                                                columnUpdater = new UpdateWrapper<>();
                                                columnUpdater.lambda().set(GenTableColumn::getOldIsPk, GenTableColumn.NO);
                                                columnUpdater.lambda().eq(GenTableColumn::getId, genTableColumn.getId());
                                                genTableColumnMapper.update(null, columnUpdater);
                                            }
                                        }

                                        if (jdbcType.length() > 0) {
                                            sb.append("alter table " + orgTable.getName() + " add  CONSTRAINT PK_SJ_RESOURCE_CHARGES PRIMARY KEY(" + jdbcType.substring(0, jdbcType.length() - 1) + ");");
                                            this.buildTable("alter table " + orgTable.getName() + " add  CONSTRAINT PK_SJ_RESOURCE_CHARGES PRIMARY KEY(" + jdbcType.substring(0, jdbcType.length() - 1) + ");");
                                            iterator = columnList.iterator();

                                            while (iterator.hasNext()) {
                                                genTableColumn = (GenTableColumn) iterator.next();
                                                if (GenTableColumn.YES.equals(genTableColumn.getIsPk())) {
                                                    columnUpdater = new UpdateWrapper<>();
                                                    columnUpdater.lambda().set(GenTableColumn::getOldIsPk, GenTableColumn.YES);
                                                    columnUpdater.lambda().eq(GenTableColumn::getId, genTableColumn.getId());
                                                    genTableColumnMapper.update(null, columnUpdater);
                                                }
                                            }
                                        }
                                    }

                                    break mark;
                                }
                                genTableColumn = (GenTableColumn) iterator.next();
                                oldColumnName = genTableColumn.getOldName();
                                newColumnName = genTableColumn.getName();
                            } while (genTableColumn.getDelFlag().equals(GenTableColumn.DEL_FLAG_DELETE));
                        } while (oldColumnName == null);
                    } while (newColumnName.equals(oldColumnName) && genTableColumn.getJdbcType().equals(genTableColumn.getOldJdbcType())
                            && genTableColumn.getComments().equals(genTableColumn.getOldComments()));

                    columnName = StringUtils.isBlank(genTableColumn.getOldName()) ? genTableColumn.getName() : genTableColumn.getOldName();
                    sb.append("alter table " + orgTable.getName() + " change  " + oldColumnName + " " + newColumnName + " " + genTableColumn.getJdbcType() + " comment '" + genTableColumn.getComments() + "';");
                    this.buildTable("alter table " + orgTable.getName() + " change  " + columnName + " " + genTableColumn.getName() + " " + genTableColumn.getJdbcType() + " comment '" + genTableColumn.getComments() + "';");
                    columnUpdater = new UpdateWrapper<>();
                    columnUpdater.lambda().set(GenTableColumn::getOldName, genTableColumn.getName());
                    columnUpdater.lambda().set(GenTableColumn::getOldComments, genTableColumn.getComments());
                    columnUpdater.lambda().set(GenTableColumn::getOldJdbcType, genTableColumn.getJdbcType());
                    columnUpdater.lambda().set(GenTableColumn::getOldIsPk, genTableColumn.getIsPk());
                    columnUpdater.lambda().eq(GenTableColumn::getId, genTableColumn.getId());
                    genTableColumnMapper.update(null, columnUpdater);
                }

            }
            // 强制同步或是没被创建过的新表（没有oldName意味着新表）
            else {
                // 建表时先将默认字段添加到allColumnList中
                setDefaultColumns(allColumnList);
                sb = new StringBuffer();
                if (StringUtils.isNotBlank(orgTable.getOldName())) {
                    sb.append("drop table if exists " + orgTable.getOldName() + " ;");
                } else {
                    sb.append("drop table if exists " + orgTable.getName() + " ;");
                }

                this.buildTable(sb.toString());
                tableUpdater = new UpdateWrapper<>();
                tableUpdater.lambda().set(GenTable::getOldName, orgTable.getName());
                tableUpdater.lambda().eq(GenTable::getId, orgTable.getId());
                update(tableUpdater);
                sb = new StringBuffer();
                sb.append("create table " + orgTable.getName() + " (");
                pk = "";
                iterator = allColumnList.iterator();

                while (iterator.hasNext()) {
                    genTableColumn = (GenTableColumn) iterator.next();
                    if (!genTableColumn.getDelFlag().equals(GenTableColumn.DEL_FLAG_DELETE) && genTableColumn.getName() != null) {
                        if (genTableColumn.getIsPk().equals(GenTableColumn.YES)) {
                            if (orgTable.getGenIdType().equals(GenTable.IDTYPE_AUTO)) {
                                jdbcType = genTableColumn.getJdbcType();
                                if (!jdbcType.toLowerCase().contains("int") && !jdbcType.toLowerCase().contains("integer")) {
                                    jdbcType = "integer";
                                    genTableColumn.setJdbcType(jdbcType);
                                }

                                sb.append("  " + genTableColumn.getName() + " " + genTableColumn + " auto_increment  comment '" + genTableColumn.getComments() + "',");
                            } else {
                                jdbcType = genTableColumn.getJdbcType();
                                if (!jdbcType.toLowerCase().contains("varchar")) {
                                    jdbcType = "varchar(64)";
                                    genTableColumn.setJdbcType(jdbcType);
                                }

                                sb.append("  " + genTableColumn.getName() + " " + jdbcType + " comment '" + genTableColumn.getComments() + "',");
                            }

                            pk = pk + genTableColumn.getName() + ",";
                        } else {
                            sb.append("  " + genTableColumn.getName() + " " + genTableColumn.getJdbcType() + " comment '" + genTableColumn.getComments() + "',");
                        }
                    }
                }

                if (StringUtils.isNotEmpty(pk)) {
                    sb.append("primary key (" + pk.substring(0, pk.length() - 1) + ") ");
                }
                sb.append(") comment '" + orgTable.getComments() + "' DEFAULT CHARSET=utf8");
                this.buildTable(sb.toString()); // 重新建表成功!
            }
            // DDL后更新gen_table和gen_table_column记录
            updateRecord(orgTable);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("数据异常，请重新操作");
        }
    }

    /**
     * 添加每张表都有的默认字段
     * @param allColumnList
     */
    private void setDefaultColumns(List<GenTableColumn> allColumnList) {
        allColumnList.add(0, new GenTableColumn(GenTableColumn.PK_NAME, "主键", "varchar(32)", "1"));
        allColumnList.add(new GenTableColumn("create_by", "创建者", "varchar(64)"));
        allColumnList.add(new GenTableColumn("create_time", "创建时间", "datetime"));
        allColumnList.add(new GenTableColumn("update_by", "更新者", "varchar(64)"));
        allColumnList.add(new GenTableColumn("update_time", "更新时间", "datetime"));
        allColumnList.add(new GenTableColumn("remark", "备注信息", "nvarchar(255)"));
        allColumnList.add(new GenTableColumn("del_flag", "删除标志（0代表存在 1代表删除)", "char(1)"));
    }

    /**
     * 同步完成后更新记录
     * @param genTable
     */
    private void updateRecord(GenTable genTable) {
        genTable.setIsSync(GenTable.SYNCHED);
        genTable.setOldName(genTable.getName());
        genTable.setOldComments(genTable.getComments());
        genTable.setOldGenIdType(genTable.getGenIdType());
        Iterator iterator = genTable.getAllColumnList().iterator();

        while (iterator.hasNext()) {
            GenTableColumn genTableColumn = (GenTableColumn) iterator.next();
            if (GenTableColumn.DEL_FLAG_DELETE.equals(genTableColumn.getDelFlag())) {
                genTableColumnMapper.deleteById(genTableColumn.getId()); // 同步完成后物理删除记录
            } else {
                genTableColumn.setOldComments(genTableColumn.getComments());
                genTableColumn.setOldIsPk(genTableColumn.getIsPk());
                genTableColumn.setOldJdbcType(genTableColumn.getJdbcType());
                genTableColumn.setOldName(genTableColumn.getName());
                genTableColumnMapper.updateById(genTableColumn);
            }
        }

        updateById(genTable);
    }


    /**
     * 判断字段名是否存在于指定表
     * @param tableName
     * @param columnName
     * @return
     */
    private boolean isColumnExistInTable(String tableName, String columnName) {
        List<GenTableColumn> columnList = genTableColumnMapper.selectTableColumnListByTableName(tableName);
        Iterator iterator = columnList.iterator();

        GenTableColumn genTableColumn;
        do {
            if (!iterator.hasNext()) {
                return false;
            }

            genTableColumn = (GenTableColumn) iterator.next();
        } while (columnName == null || !columnName.equals(genTableColumn.getName()));

        return true;
    }

    /**
     * 验证table是否已经存在
     * @param table
     */
    public boolean checkIsTableExist(GenTable table) {
        // gen_table中是否存在
        QueryWrapper<GenTable> query = new QueryWrapper<>();
        query.lambda().eq(GenTable::getName, table.getName());
        List<GenTable> list = list(query);
        if (list != null && list.size() > 0) {
            if (list.get(0).getId().equals(table.getId())) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 查询指定表名的数据内容
     * @param tableName
     * @return
     */
    @Override
    public List<Object> searchTableData(String tableName) {
        String sql = "select * from " + tableName + " order by create_time desc";
        return genTableMapper.searchSql(sql);
    }

    @Override
    public Object searchDataById(String tableName, String id) {
        String sql = "select * from " + tableName + " where id = '"
                + id + "' order by create_time desc";
        return genTableMapper.searchOne(sql);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTableData(Map<String, String[]> requestMap) {

        try {
            StringBuilder sb = new StringBuilder();
            String tableId = requestMap.get("tableId")[0];
            GenTable genTable = selectTableById(tableId);
            List<GenTableColumn> columnList = genTable.getColumnList();

            sb.append("INSERT INTO ");
            sb.append(genTable.getName());

            // 列名
            sb.append(" (");
            sb.append(GenTableColumn.PK_NAME).append(",");   // 默认主键
            sb.append("create_time").append(",");             // 创建时间
            for (GenTableColumn column : columnList) {
                sb.append(column.getName()).append(",");
            }
            sb.deleteCharAt(sb.length() - 1).append(")");

            sb.append(" VALUES (");
            sb.append("'" + UUID.randomUUID().toString().replaceAll("-", "") + "'").append(",");  // 主键的值
            sb.append("'" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "'").append(",");             // 操作时间
            // 列值
            for (GenTableColumn column : columnList) {
                String value;
                // 判断自定义添加的字段是否为主键
                if (GenTableColumn.YES.equals(column.getIsPk())) {
                    value = UUID.randomUUID().toString().replaceAll("-", "");
                    sb.append("'" + value + "'");
                } else {
                    value = requestMap.get(column.getName())[0];
                    if (StringUtils.isEmpty(value)) {
                        sb.append("NULL");
                    } else {
                        sb.append("'" + value + "'");
                    }
                }
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1).append(");");

            jdbcTemplate.execute(sb.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BusinessException("新增失败，请检查数据长度或类型是否匹配：" + ex.getMessage());
        }

        return true;
    }

    @Override
    public boolean updateTableData(Map<String, String[]> requestMap, String currentUserId) {

        try {
            StringBuilder sb = new StringBuilder();
            String tableId = requestMap.get("tableId")[0];
            GenTable genTable = selectTableById(tableId);
            List<GenTableColumn> columnList = genTable.getColumnList();
            sb.append(" UPDATE " + genTable.getName());
            sb.append(" SET ");

            for (GenTableColumn column : columnList) {
                sb.append(column.getName()).append(" = ");
                String value = requestMap.get(column.getName())[0];
                // varchar 可以保存空字符串''
                if (StringUtils.isNotEmpty(value) || "varchar".equals(column.getViewJdbc())) {
                    sb.append("'").append(value).append("'");
                } else {
                    sb.append("NULL");
                }
                sb.append(",");
            }
            sb.append(" update_by = '").append(currentUserId).append("', ");
            sb.append(" update_time = '" + DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", new Date()) + "'");
            sb.append(" WHERE");
            sb.append(" id = '" + requestMap.get("id")[0] + "'");

            jdbcTemplate.execute(sb.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BusinessException("更新失败");
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTableData(String ids, String tableId) {

        try {
            GenTable table = getById(tableId);
            String[] oidArray = ids.split(",");
            StringBuffer sb = new StringBuffer();
            sb.append("delete from  " + table.getName() + " where id in ( ");
            for (int i = 0; i < oidArray.length; i++) {
                if (i == 0) {
                    sb.append("'" + oidArray[0] + "'");
                } else {
                    sb.append(", '" + oidArray[i] + "'");
                }
            }
            sb.append(" );");
            String sql = sb.toString();

            jdbcTemplate.execute(sql);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BusinessException("删除失败：" + ex.getMessage());
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importData(List<Map<String, Object>> list, String tableId) {
        StringBuilder failureMsg = new StringBuilder();
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //计数
        int count_insert = 0;
        int count_failed = 0;

        //根据id查询未删除字段
        GenTable table = selectTableById(tableId);
        List<GenTableColumn> columnList = table.getColumnList();

        try {
            for (int i = 0; i < list.size(); i++) {
                try {
                    Map<String, Object> row = list.get(i);
                    Map<String,Object> map = new HashMap<>();
                    //循环遍历columnList
                    for (GenTableColumn column:columnList){
                        for (String key : row.keySet()) {
                            if (key.equals(column.getComments())){
                                //如果key为comments的值,map存入name和value,拼接sql
                                map.put(column.getName(),row.get(key));
                            }
                        }
                    }
                    //拼接sql
                    String createSql = createInsertSQL(table.getName(), map, columnList);
                    jdbcTemplate.execute(createSql);
                    count_insert++;
                } catch (Exception ex) {
                    //错误计数
                    count_failed++;
                    failureMsg.append("<br/>第" + (i + 2) + "行数据存在错误; ");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //不做处理
        }

        if (count_failed == 0) {
            return "导入数据成功：插入-[" + count_insert + "]条数据";
        } else if (count_insert == 0) {
            return null;
        } else {
            return "导入数据成功：插入-[" + count_insert + "]条数据，失败-["
                    + count_failed + "]条数据。信息：" + failureMsg.toString();
        }

    }

    /**
     * 创建导入语句
     * @param tableName
     * @param map
     * @param columnList
     * @return
     */
    private String createInsertSQL(String tableName, Map<String, Object> map, List<GenTableColumn> columnList) {

        StringBuffer values = new StringBuffer();
        StringBuffer keys = new StringBuffer();
        StringBuffer sql = new StringBuffer();
        String cmdDate = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"); // 操作时间
        sql.append("INSERT INTO " + tableName + " (");

        //业务字段
        for (Object key : map.keySet()) {
            keys.append(key.toString()).append(",");
            Object val = map.get(key);
            if (val == null || val == "" || "null".equals(val)) {
                val = null;
                values.append(val).append(",");
            } else {
                values.append("'").append(val).append("'").append(",");
            }
        }
        //常规字段
        keys.append("id").append(",");
        values.append("'").append(IdUtil.simpleUUID()).append("'").append(",");
        keys.append("create_time").append(",");
        values.append("'").append(cmdDate).append("'").append(",");
        keys.append("update_time");
        values.append("'").append(cmdDate).append("'");

        String str_key = keys.toString();
        String str_value = values.toString();
        sql.append(str_key).append(") values(");
        sql.append(str_value).append(")");
        sql.append(" on duplicate key update id='" + map.get("id") + "'");
        return sql.toString();
    }

    /**
     * 验证是否有字段变更
     */
    private boolean checkNeedSynch(GenTable newTable, GenTable oldTable) {
        if (!oldTable.getName().equals(newTable.getName())) {
            return true;
        }
        if (!oldTable.getComments().equals(newTable.getComments())) {
            return true;
        }
        if (!oldTable.getGenIdType().equals(newTable.getGenIdType())) {
            return true;
        }
        List<GenTableColumn> oldColumns = oldTable.getAllColumnList();
        List<GenTableColumn> newColumns = newTable.getColumnList(); // 页面传来的column放在columnList属性中
        for (GenTableColumn oldColumn : oldColumns) {
            boolean isMatched = false;
            for (GenTableColumn newColumn : newColumns) {
                if (oldColumn.getId().equals(newColumn.getId())) {
                    isMatched = true;
                    if (!oldColumn.getName().equals(newColumn.getName())
                            || !oldColumn.getComments().equals(newColumn.getComments())
                            || !oldColumn.getIsPk().equals(newColumn.getIsPk())
                            || !oldColumn.getJdbcType().equals(newColumn.getJdbcType())) {
                        return true;
                    }
                }
            }
            if (!isMatched) {
                return true; // 记录被删除了
            }
        }

        for (GenTableColumn newColumn : newColumns) {
            boolean isMatched = false;
            for (GenTableColumn oldColumn : oldColumns) {
                if (oldColumn.getId().equals(newColumn.getId())) {
                    isMatched = true;
                }
            }
            if (!isMatched) {
                return true; // 记录新增
            }
        }
        return false;
    }


}
