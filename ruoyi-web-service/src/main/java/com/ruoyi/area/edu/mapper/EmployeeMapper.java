package com.ruoyi.area.edu.mapper;

import com.ruoyi.area.edu.domain.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 人员 数据层
 *
 * @author jiyunsoft
 * @date 2019-08-26
 */
public interface EmployeeMapper extends BaseMapper<Employee> {

    /**
     * 人员列表数据查询
     * @param employee
     * @return
     */
    List<Employee> selectList(Employee employee);

    /**
     * 根据id关联查询
     * @param id
     * @return
     */
    Employee mySelectById(String id);
}