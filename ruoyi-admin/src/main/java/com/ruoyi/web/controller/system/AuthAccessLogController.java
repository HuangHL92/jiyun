package com.ruoyi.web.controller.system;
import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
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
import com.ruoyi.system.domain.AuthAccessLog;
import com.ruoyi.system.service.IAuthAccessLogService;
import com.ruoyi.framework.web.base.BaseController;
import com.ruoyi.common.page.TableDataInfo;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.support.Convert;
import javax.servlet.http.HttpServletRequest;

/**
 * 客户端访问日志 信息操作处理
 *
 * @author jiyunsoft
 * @date 2019-08-28
 */
@Controller
@RequestMapping("/system/authAccessLog")
public class AuthAccessLogController extends BaseController
{
    private String prefix = "system/authAccessLog";

    @Autowired
    private IAuthAccessLogService authAccessLogService;

    @RequiresPermissions("system:authAccessLog:view")
    @GetMapping()
    public String authAccessLog()
    {
        return prefix + "/list";
    }

    /**
     * 查询客户端访问日志列表
     */
    @RequiresPermissions("system:authAccessLog:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(AuthAccessLog authAccessLog)
    {
        startPage();
        PageHelper.clearPage();
        return getDataTable(authAccessLogService.selectList(authAccessLog));
    }


    /**
     * 导出客户端访问日志列表
     */
    @RequiresPermissions("system:authAccessLog:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(AuthAccessLog authAccessLog)
    {
        List<AuthAccessLog> list = authAccessLogService.selectList(authAccessLog);
        ExcelUtil<AuthAccessLog> util = new ExcelUtil<AuthAccessLog>(AuthAccessLog.class);
        return util.exportExcel(list, "authAccessLog");
    }

    /**
     * 新增客户端访问日志
     */
//    @GetMapping("/add")
//    public String add(ModelMap mmap)
//    {
//        AuthAccessLog authAccessLog  = new AuthAccessLog();
//        //表单Action指定
//        authAccessLog.setFormAction(prefix + "/add");
//        mmap.put("authAccessLog", authAccessLog);
//        return prefix + "/add";
//    }
//
//    /**
//     * 新增保存客户端访问日志
//     */
//    @RequiresPermissions("system:authAccessLog:add")
//    @Log(title = "客户端访问日志", businessType = BusinessType.INSERT)
//    @PostMapping("/add")
//    @ResponseBody
//    public AjaxResult addSave(AuthAccessLog authAccessLog,HttpServletRequest request, Model model)
//    {
//        return toAjax(authAccessLogService.save(authAccessLog));
//    }
//
//    /**
//     * 修改客户端访问日志
//     */
//    @GetMapping("/edit/{id}")
//    public String edit(@PathVariable("id") String id, ModelMap mmap)
//    {
//        AuthAccessLog authAccessLog = authAccessLogService.getById(id);
//        //表单Action指定
//        authAccessLog.setFormAction(prefix + "/edit");
//        //主键加密（配合editSave方法使用）- 如果需防止数据ID泄露，请放开，否则请删除此处代码
//        //authAccessLog.setId(pk_encrypt(authAccessLog.getId()));
//
//        mmap.put("authAccessLog", authAccessLog);
//        return prefix + "/add";
//    }
//
//    /**
//     * 修改保存客户端访问日志
//     */
//    @RequiresPermissions("system:authAccessLog:edit")
//    @Log(title = "客户端访问日志", businessType = BusinessType.UPDATE)
//    @PostMapping("/edit")
//    @ResponseBody
//    public AjaxResult editSave(AuthAccessLog authAccessLog)
//    {
//
//        //主键解密（配合edit方法使用，请确认edit方法中加密了）- 如果需防止数据ID泄露，请放开，否则请删除此处代码
//        //authAccessLog.setId(pk_decrypt(authAccessLog.getId()));
//
//        return toAjax(authAccessLogService.saveOrUpdate(authAccessLog));
//    }

    /**
     * 删除客户端访问日志
     */
    @RequiresPermissions("system:authAccessLog:remove")
    @Log(title = "客户端访问日志", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(authAccessLogService.removeByIds(Arrays.asList(Convert.toStrArray(ids))));
    }

}

