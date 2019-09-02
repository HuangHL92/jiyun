package com.ruoyi.area.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import com.ruoyi.area.edu.mapper.EmployeeMapper;
import com.ruoyi.area.edu.domain.Employee;
import com.ruoyi.area.edu.service.IEmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * 人员 服务层实现
 *
 * @author jiyunsoft
 * @date 2019-08-26
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {
    @Override
    public List<Employee> selectList(Employee employee) {
        return baseMapper.selectList(employee);
    }

    @Override
    public Employee selectById(String id) {
        return baseMapper.mySelectById(id);
    }
}
