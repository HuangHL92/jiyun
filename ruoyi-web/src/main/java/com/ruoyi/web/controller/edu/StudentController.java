package com.ruoyi.web.controller.edu;

import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.area.edu.domain.Tag;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.service.ISysDeptService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.area.edu.domain.Student;
import com.ruoyi.area.edu.service.IStudentService;
import com.ruoyi.framework.web.base.BaseController;
import com.ruoyi.common.page.TableDataInfo;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.support.Convert;
import org.springframework.web.multipart.MultipartFile;

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
    @Autowired
    private ISysDeptService deptService;

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
    public String student(ModelMap mmap) {
        //初始化
        init(mmap);
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
        //账号状态：默认正常
        student.setStatus("0");
        //初始化
        init(mmap);
        return prefix + "/add";
    }

    /**
     * 新增保存学生
     */
    @RequiresPermissions("edu:student:add")
    @Log(title = "学生管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Student student) {
        return studentService.doSave(student);
    }

    /**
     * 修改学生
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, ModelMap mmap) {
        Student student = studentService.getById(id);
        //表单Action指定
        student.setFormAction(prefix + "/edit");

        //初始化
        init(mmap);
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
        return studentService.doSave(student);
    }

    /**
     * 删除学生
     */
    @RequiresPermissions("edu:student:remove")
    @Log(title = "学生管理", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        // TODO
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
        // TODO
        return toAjax(studentService.updateById(student));
    }

    /**
     * 导出学生列表
     */
    @Log(title = "学生管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("edu:student:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(Student student) {
        List<Student> list = studentService.selectList(student);
        ExcelUtil<Student> util = new ExcelUtil<Student>(Student.class);
        return util.exportExcel(list, "student");

    }

    @Log(title = "学生管理", businessType = BusinessType.IMPORT)
    @RequiresPermissions("edu:student:import")
    @PostMapping("/importData")
    @ResponseBody
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<Student> util = new ExcelUtil<>(Student.class);
        List<Student> studentList = util.importExcel(file.getInputStream());
        String message = studentService.importData(studentList, updateSupport);
        return success(message);
    }

    @RequiresPermissions("edu:student:import")
    @GetMapping("/importTemplate")
    @ResponseBody
    public AjaxResult importTemplate() {
        ExcelUtil<Student> util = new ExcelUtil<>(Student.class);
        return util.importTemplateExcel("学生管理数据");
    }


    /**
     * 校验学号是否唯一
     */
    @PostMapping("/checkSnoUnique")
    @ResponseBody
    public String checkSnoUnique(Student student) {
        return studentService.checkSnoUnique(student);
    }

    /**
     * 初始化
     *
     * @param mmap
     */
    private void init(ModelMap mmap) {
        //学校下拉框内容
        List<SysDept> schoolList = deptService.selectListByTagCode(Tag.TAG_CODE_JGLX_XX);
        mmap.put("schoolList", schoolList);
    }
}
