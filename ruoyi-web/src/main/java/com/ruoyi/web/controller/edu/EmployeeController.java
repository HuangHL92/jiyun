package com.ruoyi.web.controller.edu;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.area.edu.domain.Employee;
import com.ruoyi.area.edu.service.IEmployeeService;
import com.ruoyi.framework.web.base.BaseController;
import com.ruoyi.common.page.TableDataInfo;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.support.Convert;

import javax.servlet.http.HttpServletRequest;

/**
 * 人员 信息操作处理
 *
 * @author jiyunsoft
 * @date 2019-08-26
 */
@Controller
@RequestMapping("/edu/employee")
public class EmployeeController extends BaseController {
    private String prefix = "edu/employee";

    @Autowired
    private IEmployeeService employeeService;

    @RequiresPermissions("edu:employee:view")
    @GetMapping()
    public String employee() {
        return prefix + "/list";
    }

    /**
     * 查询人员列表
     */
    @RequiresPermissions("edu:employee:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Employee employee) {
        startPage();
        return getDataTable(employeeService.selectList(employee));
    }


    /**
     * 导出人员列表
     */
    @RequiresPermissions("edu:employee:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(Employee employee) {
        List<Employee> list = employeeService.selectList(employee);
        ExcelUtil<Employee> util = new ExcelUtil<Employee>(Employee.class);
        return util.exportExcel(list, "employee");
    }

    /**
     * 新增人员
     */
    @GetMapping("/add")
    public String add(ModelMap mmap) {
        Employee employee = new Employee();
        //表单Action指定
        employee.setFormAction(prefix + "/add");
        mmap.put("employee", employee);
        return prefix + "/add";
    }

    /**
     * 新增保存人员
     */
    @RequiresPermissions("edu:employee:add")
    @Log(title = "人员", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Employee employee, HttpServletRequest request, Model model) {
        return toAjax(employeeService.save(employee));
    }

    /**
     * 修改人员
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, ModelMap mmap) {
        Employee employee = employeeService.getById(id);
        //表单Action指定
        employee.setFormAction(prefix + "/edit");

        mmap.put("employee", employee);
        return prefix + "/add";
    }

    /**
     * 修改保存人员
     */
    @RequiresPermissions("edu:employee:edit")
    @Log(title = "人员", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(Employee employee) {
        return toAjax(employeeService.saveOrUpdate(employee));
    }

    /**
     * 删除人员
     */
    @RequiresPermissions("edu:employee:remove")
    @Log(title = "人员", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(employeeService.removeByIds(Arrays.asList(Convert.toStrArray(ids))));
    }

}
