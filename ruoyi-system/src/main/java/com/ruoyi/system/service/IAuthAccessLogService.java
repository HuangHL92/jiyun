package com.ruoyi.system.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.AuthAccessLog;

import java.util.List;

/**
 * 客户端访问日志 服务层
 *
 * @author jiyunsoft
 * @date 2019-08-28
 */
public interface IAuthAccessLogService extends IService<AuthAccessLog>
{
    List<AuthAccessLog> selectList(AuthAccessLog authAccessLog);
}


