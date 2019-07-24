package com.ruoyi.area.demo.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.area.demo.domain.Department;
import com.ruoyi.area.demo.mapper.DepartmentMapper;
import com.ruoyi.area.demo.service.IDepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 练习_部门 服务层实现
 * 
 * @author jiyunsoft
 * @date 2019-02-14
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements IDepartmentService
{

    @Override
    public List<Department> selectList(Department department) {
        QueryWrapper<Department> query = new QueryWrapper<>();
        // 部门名称模糊查询
        query.lambda().like(StrUtil.isNotBlank(department.getName()), Department::getName, department.getName());
        // 部门级别
        query.lambda().eq(StrUtil.isNotBlank(department.getGrade()), Department::getGrade, department.getGrade());
        // 创办日期范围查询
        String startEstablishmentDate = department.getParams().isEmpty() ? null : department.getParams().get("startEstablishmentDate").toString();
        if (StrUtil.isNotBlank(startEstablishmentDate)) {
            query.lambda().ge(Department::getEstablishmentDate, DateUtil.parse(startEstablishmentDate, "yyyy-MM-dd"));
        }
        String endEstablishmentDate = department.getParams().isEmpty() ? null : department.getParams().get("endEstablishmentDate").toString();
        if (StrUtil.isNotBlank(endEstablishmentDate)) {
            query.lambda().le( Department::getEstablishmentDate, DateUtil.parse(endEstablishmentDate, "yyyy-MM-dd"));
        }
        // 关键字模糊查询（联系人/联系电话/联系地址）
        String keyword = department.getParams().isEmpty() ? null : department.getParams().get("keyword").toString();
        query.lambda().and(
                StrUtil.isNotBlank(keyword),
                i -> i.like(Department::getContact, keyword)
                        .or().like(Department::getContactNumber, keyword)
                        .or().like(Department::getContactAddress, keyword));
        return list(query);
    }
}
