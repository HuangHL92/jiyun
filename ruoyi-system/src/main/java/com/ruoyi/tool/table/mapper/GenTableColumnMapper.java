package com.ruoyi.tool.table.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.tool.table.domain.GenTableColumn;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GenTableColumnMapper extends BaseMapper<GenTableColumn>
{

    public List<GenTableColumn> selectTableColumnListByTableName(@Param("name") String tableName);

}
