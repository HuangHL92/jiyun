package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysDataX;
import com.ruoyi.system.mapper.SysDataXMapper;
import com.ruoyi.system.service.ISysDataXService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Datax配置 服务层实现
 * 
 * @author jiyunsoft
 * @date 2019-04-02
 */
@Service
public class SysDataXServiceImpl extends ServiceImpl<SysDataXMapper, SysDataX> implements ISysDataXService
{

    @Override
    public List<SysDataX> selectList(SysDataX sysDataX) {
        QueryWrapper<SysDataX> query = new QueryWrapper<>();
        // 查询条件
        String keyword = sysDataX.getParams().isEmpty()?null: sysDataX.getParams().get("keyword").toString();
        query.lambda().and(StrUtil.isNotBlank(keyword),i->i.like(SysDataX::getFileName,keyword));
        return list(query);
    }



}
