package com.ruoyi.system.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.AuthAccessLogMapper;
import com.ruoyi.system.domain.AuthAccessLog;
import com.ruoyi.system.service.IAuthAccessLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;

/**
 * 客户端访问日志 服务层实现
 *
 * @author jiyunsoft
 * @date 2019-08-28
 */
@Service
public class AuthAccessLogServiceImpl extends ServiceImpl<AuthAccessLogMapper, AuthAccessLog> implements IAuthAccessLogService
{
    @Autowired
    private AuthAccessLogMapper authAccessLogMapper;
    @Override
    public List<AuthAccessLog> selectList(AuthAccessLog authAccessLog) {


        return authAccessLogMapper.selectList(authAccessLog);
    }
}


