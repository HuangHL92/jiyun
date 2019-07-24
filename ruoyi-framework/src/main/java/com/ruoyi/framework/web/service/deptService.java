package com.ruoyi.framework.web.service;

import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RuoYi首创 html调用 thymeleaf 实现参数管理
 * 
 * @author ruoyi
 */
@Service("dept")
public class deptService
{
    @Autowired
    private ISysDeptService deptService;



    public String getName(String id)
    {
        SysDept dept= deptService.selectDeptById(id);
        return dept==null?"":dept.getDeptName();
    }

    public List<SysDept> getList()
    {
        //TODO 需根据order_num和parent_id排序
        List<SysDept> list = deptService.selectDeptList(new SysDept());
        for (SysDept dept:list) {
            int lv = dept.getAncestors().split(",").length;

        }
        return list;
    }
}
