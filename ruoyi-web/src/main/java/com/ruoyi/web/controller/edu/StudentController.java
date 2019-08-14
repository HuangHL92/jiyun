package com.ruoyi.web.controller.edu;

import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.area.edu.domain.Student;
import com.ruoyi.area.edu.service.IStudentService;
import com.ruoyi.framework.web.base.BaseController;
import com.ruoyi.common.page.TableDataInfo;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.support.Convert;

import javax.servlet.http.HttpServletRequest;

/**
 * 学生 信息操作处理
 *
 * @author jiyunsoft
 * @date 2019-08-14
 */
@Controller
@RequestMapping("/edu/student")
public class StudentController extends BaseController {
    private String prefix = "edu/student";

    @Autowired
    private IStudentService studentService;



    @ModelAttribute
    public Student get(@RequestParam(required = false) String id) {
        if (StrUtil.isNotBlank(id)) {
            return studentService.getById(id);
        } else {
            return new Student();
        }
    }

    @RequiresPermissions("edu:student:view")
    @GetMapping()
    public String student() {
        return prefix + "/list";
    }

    /**
     * 查询学生列表
     */
    @RequiresPermissions("edu:student:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Student student) {
        startPage();
        return getDataTable(studentService.selectList(student));
    }

    /**
     * 新增学生
     */
    @GetMapping("/add")
    public String add(ModelMap mmap) {
        Student student = new Student();
        //表单Action指定
        student.setFormAction(prefix + "/add");
        mmap.put("student", student);
        return prefix + "/add";
    }

    /**
     * 新增保存学生
     */
    @RequiresPermissions("edu:student:add")
    @Log(title = "学生管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Student student, HttpServletRequest request, Model model) {
        // TODO 此处关联操作用户表
        student.setUserId("123456");
        return toAjax(studentService.save(student));
    }

    /**
     * 修改学生
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, ModelMap mmap) {
        Student student = studentService.getById(id);
        //表单Action指定
        student.setFormAction(prefix + "/edit");
        //主键加密（配合editSave方法使用）- 如果需防止数据ID泄露，请放开，否则请删除此处代码
        //student.setId(pk_encrypt(student.getId()));

        mmap.put("student", student);
        return prefix + "/add";
    }

    /**
     * 修改保存学生
     */
    @RequiresPermissions("edu:student:edit")
    @Log(title = "学生管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(Student student) {

        //主键解密（配合edit方法使用，请确认edit方法中加密了）- 如果需防止数据ID泄露，请放开，否则请删除此处代码
        //student.setId(pk_decrypt(student.getId()));

        return toAjax(studentService.saveOrUpdate(student));
    }

    /**
     * 删除学生
     */
    @RequiresPermissions("edu:student:remove")
    @Log(title = "学生管理", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(studentService.removeByIds(Arrays.asList(Convert.toStrArray(ids))));
    }

    /**
     * 客户端状态修改
     */
    @Log(title = "学生管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("edu:student:edit")
    @PostMapping("/changeStatus")
    @ResponseBody
    public AjaxResult changeStatus(Student student) {
        return toAjax(studentService.updateById(student));
    }

    /**
     * 导出学生列表
     */
    @RequiresPermissions("edu:student:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(Student student) {
        List<Student> list = studentService.selectList(student);
        ExcelUtil<Student> util = new ExcelUtil<Student>(Student.class);
        return util.exportExcel(list, "student");

    }
}
