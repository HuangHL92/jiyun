package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.SysDataX;

import java.util.List;

/**
 * Datax配置 服务层
 * 
 * @author jiyunsoft
 * @date 2019-04-02
 */
public interface ISysDataXService extends IService<SysDataX>
{
    List<SysDataX> selectList(SysDataX sysDataX);
}
