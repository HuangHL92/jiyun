package com.ruoyi.area.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.area.demo.domain.Demo;

import java.util.List;

/**
 * 测试 服务层
 * 
 * @author ruoyi
 * @date 2019-01-18
 */

public interface IDemoService extends IService<Demo>
{
    List<Demo> selectList(Demo sysDemo);

    Page<Demo> selectList4Page1(Page<Demo> page);

    Page<Demo> selectList4Page2(Page<Demo> page, Demo sysDemo);

}