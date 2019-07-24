package com.ruoyi.framework.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ruoyi.common.config.Global;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysUserService;

import java.io.File;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Json文件 工具类
 * 
 * @author ruoyi
 */
public class JsonFileUtils
{

    /**
     * 组织机构Json文件目录
     */
    public static String ORG_JSON_FILE_PATH = Global.getJsonPath() + "org.json";

    private static ISysUserService userService = SpringUtils.getBean(ISysUserService.class);
    private static ISysDeptService deptService = SpringUtils.getBean(ISysDeptService.class);


    /**
     * 获取组织机构json字符串
     * @return
     */
    public static String getOrgTreeJson(String deptId)
    {
        String orgJson;
        // json文件存在，直接读取
        if (FileUtil.exist(ORG_JSON_FILE_PATH)) {
            JSONObject jsonObject = JSONUtil.readJSONObject(new File(FileUtil.getAbsolutePath(ORG_JSON_FILE_PATH)), Charset.forName("UTF-8"));

            if (StringUtils.isNotEmpty(deptId) && !"null".equals(deptId)) {
                jsonObject = boxJsonResult(filterDept(deptId, (JSONObject) ((JSONArray) jsonObject.get("data")).get(0)));
            }

            orgJson = jsonObject != null ? jsonObject.toJSONString(4) : "";
        }
        // json文件不存在，数据库读取json并生成json文件
        else {
            // 写入文件的json对象
            JSONObject result = new JSONObject();
            JSONArray array = new JSONArray();
            bindChildByParent("0", array);
            result.put("data", array);
            // json字符串的返回对象
            JSONObject jsonObject = new JSONObject(result.toJSONString(4));  // 创建新对象不影响生成json文件

            if (StringUtils.isNotEmpty(deptId) && !"null".equals(deptId)) {
                jsonObject = boxJsonResult(filterDept(deptId, (JSONObject) ((JSONArray) result.get("data")).get(0)));
            }

            orgJson = jsonObject != null ? jsonObject.toJSONString(4) : "";
            FileUtil.writeString(result.toJSONString(4), ORG_JSON_FILE_PATH, "UTF-8");
        }
        return orgJson;
    }

    /**
     * 删除组织机构json文件
     * @return
     */
    public static void deleteOrgJsonFile()
    {
        FileUtil.del(ORG_JSON_FILE_PATH);
    }

    /**
     * 获取组织结构json
     * @param pId
     * @param array
     * @return
     */
    private static JSONArray bindChildByParent(String pId, JSONArray array) {
        SysDept parent = new SysDept();
        List<SysDept> deptList;
        if ("0".equals(pId)) {
            deptList = deptService.selectTopList(parent);
        } else{
            parent.setParentId(pId);
            deptList = deptService.selectDeptList(parent);
        }
        for (SysDept item : deptList) {
            JSONObject object = new JSONObject(new LinkedHashMap());
            object.put("id", item.getDeptId()); // id
            object.put("deptname", item.getDeptName()); // deptname
            object.put("type", "dept"); // type
            parent.setParentId(item.getDeptId());
            List<SysDept> childrenDept = deptService.selectDeptList(parent);
            JSONArray children = getUserJsonDeptId(item.getDeptId());
            if (childrenDept != null && childrenDept.size() > 0) {
                object.put("children", bindChildByParent(item.getDeptId(), children)); // children
            } else {
                object.put("children", children); // children
            }
            array.add(object);
        }
        return array;
    }

    /**
     * 根据部门id获取用户列表
     * @param deptId
     * @return
     */
    private static JSONArray getUserJsonDeptId(String deptId) {
        JSONArray array = new JSONArray();
        SysUser sysUser = new SysUser();
        sysUser.setDeptId(deptId);
        List<SysUser> userList = userService.selectUserListByDeptId(sysUser);
        for (SysUser user : userList) {
            JSONObject object = new JSONObject(new LinkedHashMap());
            object.put("id", user.getUserId()); // id
            object.put("deptname", user.getUserName()); // deptname
            object.put("type", "user"); // type
            object.put("children", new JSONArray()); // children
            array.add(object);
        }
        return array;
    }

    /**
     * 递归过滤无关的部门
     * @param deptId  目标部门
     * @param jsonObject
     * @return
     */
    private static JSONObject filterDept(String deptId, JSONObject jsonObject) {
        JSONObject cpJson = new JSONObject(jsonObject.toJSONString(4));  // 创建新对象
        JSONArray children = (JSONArray) cpJson.get("children");
        String type = (String)cpJson.get("type");
        String id = (String)cpJson.get("id");
        if ("dept".equals(type) && id.equals(deptId)) {
            return cpJson;
        }
        if ("dept".equals(type) && children != null && children.size() > 0) {
            for (Object child : children) {
                JSONObject jsonChild = (JSONObject) child;
                String childId = (String) jsonChild.get("id");
                String childType = (String) jsonChild.get("type");
                if (StringUtils.isNotEmpty(childId) && childId.equals(deptId) && "dept".equals(childType)) {
                    return jsonChild;
                } else {
                    JSONObject recursive = filterDept(deptId, jsonChild);
                    if (recursive != null) {
                        return recursive;
                    }
                }
            }
        }
        return null;   // 没匹配，返回null
    }

    /**
     * 装箱返回json结果
     * @return
     */
    private static JSONObject boxJsonResult(JSONObject jsonObject) {
        if (jsonObject == null)
            return null;
        JSONArray array = new JSONArray();
        JSONObject result = new JSONObject();
        array.add(jsonObject);
        result.put("data", array);
        return result;
    }

}
