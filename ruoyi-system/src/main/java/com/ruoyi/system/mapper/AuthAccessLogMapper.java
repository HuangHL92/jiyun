package com.ruoyi.system.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.AuthAccessLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 客户端访问日志 数据层
 *
 * @author jiyunsoft
 * @date 2019-08-28
 */
@Mapper
public interface AuthAccessLogMapper extends BaseMapper<AuthAccessLog>
{

    public  List<AuthAccessLog> selectList(AuthAccessLog authAccessLog);
}

