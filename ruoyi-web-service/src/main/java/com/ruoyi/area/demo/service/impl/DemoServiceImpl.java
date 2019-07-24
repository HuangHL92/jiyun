package com.ruoyi.area.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.area.demo.domain.Demo;
import com.ruoyi.area.demo.mapper.DemoMapper;
import com.ruoyi.area.demo.service.IDemoService;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试 服务层实现
 * 
 * @author ruoyi
 * @date 2019-01-18
 */
@Service
public class DemoServiceImpl extends ServiceImpl<DemoMapper, Demo> implements IDemoService
{
    @Override
    public List<Demo> selectList(Demo demo) {
        QueryWrapper<Demo> query = new QueryWrapper<>();
        // 查询条件
        if(StringUtils.isNotEmpty(demo.getName())) {
            query.lambda()
                    .likeRight(Demo::getName, demo.getName());

        }
        query.lambda().orderByDesc(Demo::getCreateTime);
        //query.orderByDesc("update_time");

        return list(query);
    }

    @Override
    public Page<Demo> selectList4Page1(Page<Demo> page) {
        return page.setRecords(this.baseMapper.select4page1(page));
    }

    @Override
    public Page<Demo> selectList4Page2(Page<Demo> page,Demo demo) {
        QueryWrapper<Demo> query = new QueryWrapper<>();

        // 查询条件
        if(StringUtils.isNotEmpty(demo.getName())) {
            //query.lambda().and(i-> i.like(Demo::getName,demo.getName()));  //TODO 此方式在多表状态下会存在字段名相同而引起SQL报错，不建议使用
            StringBuffer strWhere = new StringBuffer();
            strWhere = strWhere.append(" sys_demo.name like '%" + demo.getName() + "%'");
            query.apply(strWhere.toString());
        }

        return page.setRecords(this.baseMapper.select4page2(page,query));

    }


}
