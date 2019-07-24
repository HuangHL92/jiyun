package com.ruoyi.web.controller.system;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.util.JsonFileUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.framework.web.base.BaseController;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysDeptService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 部门信息
 * 
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/dept")
public class SysDeptController extends BaseController
{
    private String prefix = "system/dept";

    @Autowired
    private ISysDeptService deptService;

    @RequiresPermissions("system:dept:view")
    @GetMapping()
    public String dept()
    {
        return prefix + "/dept";
    }

    @RequiresPermissions("system:dept:list")
    @GetMapping("/list")
    @ResponseBody
    public List<SysDept> list(SysDept dept)
    {
        List<SysDept> deptList = deptService.selectDeptList(dept);
        return deptList;
    }

    /**
     * 新增部门
     */
    @GetMapping("/add/{parentId}")
    public String add(@PathVariable("parentId") String parentId, ModelMap mmap)
    {
        mmap.put("dept", deptService.selectDeptById(parentId));
        return prefix + "/add";
    }

    /**
     * 新增保存部门
     */
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @RequiresPermissions("system:dept:add")
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(SysDept dept)
    {
        dept.setCreateBy(ShiroUtils.getLoginName());
        // 删除组织结构json文件
        JsonFileUtils.deleteOrgJsonFile();
        return toAjax(deptService.insertDept(dept));
    }

    /**
     * 修改
     */
    @GetMapping("/edit/{deptId}")
    public String edit(@PathVariable("deptId") String deptId, ModelMap mmap)
    {
        SysDept dept = deptService.selectDeptById(deptId);
        if (StringUtils.isNotNull(dept) && "100".equals(deptId))
        {
            dept.setParentName("无");
        }
        mmap.put("dept", dept);
        return prefix + "/edit";
    }

    /**
     * 保存
     */
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("system:dept:edit")
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(SysDept dept)
    {
        dept.setUpdateBy(ShiroUtils.getLoginName());
        // 删除组织结构json文件
        JsonFileUtils.deleteOrgJsonFile();
        return toAjax(deptService.updateDept(dept));
    }

    /**
     * 删除
     */
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @RequiresPermissions("system:dept:remove")
    @PostMapping("/remove/{deptId}")
    @ResponseBody
    public AjaxResult remove(@PathVariable("deptId") String deptId)
    {
        if (deptService.selectDeptCount(deptId) > 0)
        {
            return error(1, "存在下级部门,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId))
        {
            return error(1, "部门存在用户,不允许删除");
        }
        // 删除组织结构json文件
        JsonFileUtils.deleteOrgJsonFile();
        return toAjax(deptService.deleteDeptById(deptId));
    }

    /**
     * 校验部门名称
     */
    @PostMapping("/checkDeptNameUnique")
    @ResponseBody
    public String checkDeptNameUnique(SysDept dept)
    {
        return deptService.checkDeptNameUnique(dept);
    }

    /**
     * 选择部门树
     */
    @GetMapping("/selectDeptTree/{deptId}")
    public String selectDeptTree(@PathVariable("deptId") String deptId, ModelMap mmap)
    {
        mmap.put("dept", deptService.selectDeptById(deptId));
        return prefix + "/tree";
    }

    /**
     * 加载部门列表树
     */
    @GetMapping("/treeData")
    @ResponseBody
    public List<Map<String, Object>> treeData()
    {
        List<Map<String, Object>> tree = deptService.selectDeptTree(new SysDept());
        return tree;
    }

    /**
     * 加载角色部门（数据权限）列表树
     */
    @GetMapping("/roleDeptTreeData")
    @ResponseBody
    public List<Map<String, Object>> deptTreeData(SysRole role)
    {
        List<Map<String, Object>> tree = deptService.roleDeptTreeData(role);
        return tree;
    }


    /**
     * 用户下拉框
     */
    @GetMapping("/getList4Select")
    @ResponseBody
    public JSONObject getList4Select(String deptid)
    {
        SysUser user = new SysUser();
        if(!StringUtils.isEmpty(deptid)) {
            user.setDeptId(deptid);
        }

        JSONObject robj = new JSONObject();
        robj.put("code",0);
        robj.put("msg","success");
        //TODO 取得用户，此处可以优化（1.放入缓存 2.数据库读取sql优化）
        //TODO 理想方案是前端直接读JSON文件
        List<Map<String, Object>> tree = deptService.selectDeptTree(new SysDept());
        JSONArray rList = new JSONArray();
        for (Map<String, Object> u: tree) {
            JSONObject o = new JSONObject();
            o.put("name",u.get("name"));
            o.put("value",u.get("id"));
            o.put("type",u.get("type"));

            rList.add(o);
        }

        robj.put("data",rList);
        return robj;
    }


    /**
     * 用户下拉框
     */
    @GetMapping("/getTree4Select")
    @ResponseBody
    public JSONArray getTree4Select(HttpServletRequest request)
    {
        String deptid  =request.getParameter("deptid");
        String keyword  =request.getParameter("keyword");

        JSONObject robj = new JSONObject();
        robj.put("code",0);
        robj.put("msg","success");

        JSONArray rList = new JSONArray();
        SysDept parent = new SysDept();
        List<SysDept> parents = deptService.selectTopList(parent);
        for (SysDept d: parents) {
            JSONObject o = new JSONObject();
            o.put("name",d.getDeptName());
            o.put("value",d.getDeptId());
            createTree(d,o,deptid);
            rList.add(o);
        }
        return rList;
    }

    private void createTree(SysDept d, JSONObject o, String pid) {

        SysDept dept = new SysDept();
        dept.setParentId(d.getDeptId());
        List<SysDept> childrens = deptService.selectDeptList(dept);

        if (childrens.size() > 0)
        {
            JSONArray cobj = new JSONArray();
            for (SysDept s: childrens) {
                JSONObject sb = new JSONObject();
                sb.put("name",s.getDeptName());
                sb.put("value",s.getDeptId());
                createTree(s,sb,pid);

                if(StringUtils.isNotEmpty(pid) ) {
                    //只显示指定部门下的子部门
                    if( pid.equals(s.getParentId()) || pid.equals(s.getDeptId())) {
                        cobj.add(sb);
                        //sb.put("disabled","true");
                    }
                } else {
                    cobj.add(sb);
                }


            }
            o.put("children",cobj);
        }
    }

    /**
     * 更新显示顺序（Ajax）
     */
    @GetMapping("/updateOrder")
    @ResponseBody
    public AjaxResult updateOrder(String id, String orderNum)
    {
        try {
            SysDept obj = deptService.selectDeptById(id);
            if (obj!=null)
            {
                obj.setOrderNum(orderNum);
                deptService.updateDept(obj);
            }
        } catch (Exception ex) {
            return AjaxResult.error("更新失败！");
        }

        return AjaxResult.success();
    }



}
