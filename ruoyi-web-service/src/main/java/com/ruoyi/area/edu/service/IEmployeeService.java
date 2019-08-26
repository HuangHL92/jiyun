package com.ruoyi.area.edu.service;

import com.ruoyi.area.edu.domain.Employee;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 人员 服务层
 *
 * @author jiyunsoft
 * @date 2019-08-26
 */
public interface IEmployeeService extends IService<Employee> {
    List<Employee> selectList(Employee employee);
}
