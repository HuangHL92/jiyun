package com.ruoyi.web.controller.system;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.config.Global;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.page.TableDataInfo;
import com.ruoyi.common.support.Convert;
import com.ruoyi.common.utils.PwdCheckUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.shiro.service.SysPasswordService;
import com.ruoyi.framework.util.CacheUtils;
import com.ruoyi.framework.util.JsonFileUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.framework.web.base.BaseController;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysPostService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用户信息
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/user")
public class SysUserController extends BaseController
{
    private String prefix = "system/user";

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private SysPasswordService passwordService;

    @Autowired
    private CacheUtils cacheUtils;

    @RequiresPermissions("system:user:view")
    @GetMapping()
    public String user()
    {
        return prefix + "/user";
    }

    @RequiresPermissions("system:user:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysUser user)
    {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:user:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SysUser user)
    {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        return util.exportExcel(list, "用户数据");
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @RequiresPermissions("system:user:import")
    @PostMapping("/importData")
    @ResponseBody
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operName = getSysUser().getLoginName();
        String message = userService.importUser(userList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @RequiresPermissions("system:user:view")
    @GetMapping("/importTemplate")
    @ResponseBody
    public AjaxResult importTemplate()
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        return util.importTemplateExcel("用户数据");
    }

    /**
     * 新增用户
     */
    @GetMapping("/add")
    public String add(ModelMap mmap)
    {
        mmap.put("roles", roleService.selectRoleAll());
        mmap.put("posts", postService.selectPostAll());
        return prefix + "/add";
    }

    /**
     * 新增保存用户
     */
    @RequiresPermissions("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public AjaxResult addSave(SysUser user)
    {
        if (StringUtils.isNotNull(user.getUserId()) && SysUser.isAdmin(user.getUserId()))
        {
            return error("不允许修改超级管理员用户");
        }
        user.setSalt(ShiroUtils.randomSalt());
        user.setPassword(passwordService.encryptPassword(user.getLoginName(), user.getPassword(), user.getSalt()));
        user.setCreateBy(ShiroUtils.getLoginName());
        // 删除组织结构json文件
        JsonFileUtils.deleteOrgJsonFile();
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @GetMapping("/edit/{userId}")
    public String edit(@PathVariable("userId") String userId, ModelMap mmap)
    {
        mmap.put("user", userService.selectUserById(userId));
        mmap.put("roles", roleService.selectRolesByUserId(userId));
        mmap.put("posts", postService.selectPostsByUserId(userId));
        return prefix + "/edit";
    }

    /**
     * 修改保存用户
     */
    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public AjaxResult editSave(SysUser user)
    {
        if (StringUtils.isNotNull(user.getUserId()) && SysUser.isAdmin(user.getUserId()))
        {
            return error("不允许修改超级管理员用户");
        }
        user.setUpdateBy(ShiroUtils.getLoginName());
        // 清空用户缓存,需要先根据用户id获取用户
        cacheUtils.getUserCache().remove(userService.selectUserById(user.getUserId()).getLoginName());
        // 清空用户认证权限缓存（修改了角色的情况）
        ShiroUtils.clearCachedAuthorizationInfo();
        // 删除组织结构json文件
        JsonFileUtils.deleteOrgJsonFile();
        return toAjax(userService.updateUser(user));
    }

    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit/userInfo")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public AjaxResult editSaveUserInfo(SysUser user){
        return toAjax(userService.updateUserInfo(user));
    }

    @RequiresPermissions("system:user:resetPwd")
    @Log(title = "重置密码", businessType = BusinessType.UPDATE)
    @GetMapping("/resetPwd/{userId}")
    public String resetPwd(@PathVariable("userId") String userId, ModelMap mmap)
    {
        mmap.put("user", userService.selectUserById(userId));
        return prefix + "/resetPwd";
    }

    @RequiresPermissions("system:user:resetPwd")
    @Log(title = "重置密码", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd")
    @ResponseBody
    public AjaxResult resetPwdSave(SysUser user)
    {
        // 特殊字符解码
        user.setPassword(StringEscapeUtils.unescapeHtml(user.getPassword()));
        // 判断密码复杂度：防止绕过前台的jquery-validation验证
        boolean passwordCheck = PwdCheckUtil.checkPasswordComplexity(user.getPassword());
        if (!passwordCheck) {
            return error(Global.getPasswordMessage());
        }

        user.setSalt(ShiroUtils.randomSalt());
        user.setPassword(passwordService.encryptPassword(user.getLoginName(), user.getPassword(), user.getSalt()));
        // 清空用户缓存
        cacheUtils.getUserCache().remove(user.getLoginName());
        return toAjax(userService.resetUserPwd(user));
    }

    @RequiresPermissions("system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        try
        {
            // 循环获取用户清空缓存（暂未实现更好的方法）
            String[] userIds = Convert.toStrArray(ids);
            for (String userId : userIds)
            {
                if (SysUser.isAdmin(userId))
                {
                    throw new BusinessException("不允许删除超级管理员用户");
                }
                cacheUtils.getUserCache().remove(userService.selectUserById(userId).getLoginName());
            }
            // 清空用户认证权限缓存
            ShiroUtils.clearCachedAuthorizationInfo();
            // 删除组织结构json文件
            JsonFileUtils.deleteOrgJsonFile();
            return toAjax(userService.deleteUserByIds(ids));
        }
        catch (Exception e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 校验用户名
     */
    @PostMapping("/checkLoginNameUnique")
    @ResponseBody
    public String checkLoginNameUnique(SysUser user)
    {
        return userService.checkLoginNameUnique(user.getLoginName());
    }

    /**
     * 校验手机号码
     */
    @PostMapping("/checkPhoneUnique")
    @ResponseBody
    public String checkPhoneUnique(SysUser user)
    {
        return userService.checkPhoneUnique(user);
    }

    /**
     * 校验email邮箱
     */
    @PostMapping("/checkEmailUnique")
    @ResponseBody
    public String checkEmailUnique(SysUser user)
    {
        return userService.checkEmailUnique(user);
    }

    /**
     * 用户状态修改
     */
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("system:user:edit")
    @PostMapping("/changeStatus")
    @ResponseBody
    public AjaxResult changeStatus(SysUser user)
    {
        // 清空用户缓存
        cacheUtils.getUserCache().remove(user.getLoginName());
        // 删除组织结构json文件
        JsonFileUtils.deleteOrgJsonFile();
        return toAjax(userService.changeStatus(user));
    }





    /**
     * 用户下拉框
     */
    @GetMapping("/getList4Select")
    @ResponseBody
    public JSONObject getList4Select(HttpServletRequest request)
    {
        String deptid  =request.getParameter("deptid");
        String keyword  =request.getParameter("keyword");
        SysUser user = new SysUser();
        if(!StringUtils.isEmpty(deptid)) {
            user.setDeptId(deptid);
        }
        if(!StringUtils.isEmpty(keyword)) {
            user.setUserName(keyword);
        }

        JSONObject robj = new JSONObject();
        robj.put("code",0);
        robj.put("msg","success");
        //TODO 取得用户，此处可以优化（1.放入缓存 2.数据库读取sql优化）
        //TODO 理想方案是前端直接读JSON文件
        List<SysUser> list = userService.selectUserList(user);
        JSONArray rList = new JSONArray();
        for (SysUser u: list) {
            JSONObject o = new JSONObject();
//            o.put("name",u.getUserName()+ "<" + u.getDept().getDeptName() + ">");
            o.put("name",u.getUserName());
            o.put("value",u.getUserId());
            o.put("deptname",u.getDept().getDeptName());
//            o.put("disabled",u.getUserId());
//            o.put("type",u.getUserId());
            rList.add(o);
        }

        robj.put("data",rList);
        return robj;
    }

    /**
     * 获取组织&用户json
     * @return
     */
    @GetMapping("/orgTree")
    @ResponseBody
    public String orgTree(@RequestParam(required = false, defaultValue = "") String deptId) {
        return JsonFileUtils.getOrgTreeJson(deptId);
    }

    /**
     * 更新显示顺序（Ajax）
     */
    @GetMapping("/updateOrder")
    @ResponseBody
    public AjaxResult updateOrder(String id, String orderNum)
    {
        try {
            SysUser obj = userService.selectUserById(id);
            if (obj!=null)
            {
                obj.setOrderNum(Long.valueOf(orderNum));
                userService.updateUserInfo(obj);
            }
        } catch (Exception ex) {
            return AjaxResult.error("更新失败！");
        }

        return AjaxResult.success();
    }

    /**
     * 用户解锁
     * @param userId
     * @return
     */
    @RequiresPermissions("system:user:unlock")
    @Log(title = "用户解锁", businessType = BusinessType.UPDATE)
    @PostMapping("/unlock")
    @ResponseBody
    public AjaxResult unlock(String userId)
    {
        SysUser user = userService.selectUserById(userId);
        if(user !=null) {
            AtomicInteger retryCount = cacheUtils.getLoginRecordCache().get(user.getLoginName());
            if(retryCount!=null) {
                retryCount.set(0);
            }
        }

        return AjaxResult.success();
    }

}