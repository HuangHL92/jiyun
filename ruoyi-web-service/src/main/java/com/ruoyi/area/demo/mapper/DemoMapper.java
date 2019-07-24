package com.ruoyi.area.demo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.area.demo.domain.Demo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 测试 数据层
 * 
 * @author ruoyi
 * @date 2019-01-18
 */
public interface DemoMapper  extends BaseMapper<Demo>
{

    @Select("SELECT sys_demo.*,sys_user.`user_name` as creator FROM sys_demo,sys_user WHERE sys_demo.create_by=sys_user.user_id")
    List<Demo> select4page1(Page page);

    /**
     * 多表查询
     * @param page
     * @param ew
     * @return
     */
    List<Demo> select4page2(Page page, @Param("ew") QueryWrapper<Demo> ew);


}