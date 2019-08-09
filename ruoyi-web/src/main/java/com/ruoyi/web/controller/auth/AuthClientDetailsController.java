package com.ruoyi.web.controller.auth;

import java.util.Arrays;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.area.auth.domain.AuthClientDetails;
import com.ruoyi.area.auth.service.IAuthClientDetailsService;
import com.ruoyi.common.utils.EncryptUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.framework.web.base.BaseController;
import com.ruoyi.common.page.TableDataInfo;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.support.Convert;


/**
 * 客户端接入管理 信息操作处理
 *
 * @author jiyunsoft
 * @date 2019-08-09
 */
@Controller
@RequestMapping("/auth/authClientDetails")
public class AuthClientDetailsController extends BaseController {
    private String prefix = "auth/authClientDetails";

    @Autowired
    private IAuthClientDetailsService authClientDetailsService;

    @ModelAttribute
    public AuthClientDetails get(@RequestParam(required = false) String id) {
        if (StrUtil.isNotBlank(id)) {
            return authClientDetailsService.getById(id);
        } else {
            return new AuthClientDetails();
        }
    }

    @RequiresPermissions("auth:authClientDetails:view")
    @GetMapping()
    public String authClientDetails() {
        return prefix + "/list";
    }

    /**
     * 查询客户端接入管理列表
     */
    @RequiresPermissions("auth:authClientDetails:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(AuthClientDetails authClientDetails) {
        startPage();
        return getDataTable(authClientDetailsService.selectList(authClientDetails));
    }

    /**
     * 新增客户端接入管理
     */
    @GetMapping("/add")
    public String add(ModelMap mmap) {
        AuthClientDetails authClientDetails = new AuthClientDetails();
        //表单Action指定
        authClientDetails.setFormAction(prefix + "/add");
        mmap.put("authClientDetails", authClientDetails);
        return prefix + "/add";
    }

    /**
     * 新增保存客户端接入管理
     */
    @RequiresPermissions("auth:authClientDetails:add")
    @Log(title = "客户端接入管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(AuthClientDetails authClientDetails) {
        return toAjax(authClientDetailsService.doSave(authClientDetails));
    }

    /**
     * 修改客户端接入管理
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, ModelMap mmap) {
        AuthClientDetails authClientDetails = authClientDetailsService.getById(id);
        //表单Action指定
        authClientDetails.setFormAction(prefix + "/edit");

        mmap.put("authClientDetails", authClientDetails);
        return prefix + "/add";
    }

    /**
     * 修改保存客户端接入管理
     */
    @RequiresPermissions("auth:authClientDetails:edit")
    @Log(title = "客户端接入管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(AuthClientDetails authClientDetails) {
        return toAjax(authClientDetailsService.doSave(authClientDetails));
    }

    /**
     * 删除客户端接入管理
     */
    @RequiresPermissions("auth:authClientDetails:remove")
    @Log(title = "客户端接入管理", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(authClientDetailsService.removeByIds(Arrays.asList(Convert.toStrArray(ids))));
    }

    /**
     * 客户端状态修改
     */
    @Log(title = "客户端接入管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("auth:authClientDetails:edit")
    @PostMapping("/changeStatus")
    @ResponseBody
    public AjaxResult changeStatus(AuthClientDetails authClientDetails) {
        return toAjax(authClientDetailsService.updateById(authClientDetails));
    }

    /**
     * 客户端状态修改
     */
    @Log(title = "客户端接入管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("auth:authClientDetails:edit")
    @PostMapping("/refresh")
    @ResponseBody
    public AjaxResult refresh(AuthClientDetails authClientDetails) {
        //生成32位随机的clientSecret
        String clientSecret = EncryptUtils.getRandomStr1(32);
        authClientDetails.setClientSecret(clientSecret);
        return toAjax(authClientDetailsService.updateById(authClientDetails));
    }
}
