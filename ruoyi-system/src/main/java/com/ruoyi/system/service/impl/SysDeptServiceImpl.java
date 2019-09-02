package com.ruoyi.system.service.impl;

import cn.hutool.core.util.IdUtil;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 部门管理 服务实现
 * 
 * @author ruoyi
 */
@Service
public class SysDeptServiceImpl implements ISysDeptService
{
    @Autowired
    private SysDeptMapper deptMapper;

    /**
     * 查询部门管理数据
     * 
     * @param dept 部门信息
     * @return 部门信息集合
     */
    @Override
    @DataScope(tableAlias = "d")
    public List<SysDept> selectDeptList(SysDept dept)
    {
        return deptMapper.selectDeptList(dept);
    }

    /**
     * 查询部门管理树
     * 
     * @param dept 部门信息
     * @return 所有部门信息
     */
    @Override
    @DataScope(tableAlias = "d")
    public List<Map<String, Object>> selectDeptTree(SysDept dept, String todo)
    {
        List<Map<String, Object>> trees = new ArrayList<Map<String, Object>>();
        List<SysDept> deptList = deptMapper.selectDeptList(dept);
        if (StringUtils.isNotEmpty(todo)) {
            // 模拟出[待分配人员]
            SysDept todoDept = new SysDept();
            todoDept.setDeptId("-1");
            todoDept.setParentId("100");
            todoDept.setAncestors("0,100");
            todoDept.setDeptName("待分配人员");
            todoDept.setOrgStructure("待分配人员");
            todoDept.setStatus(UserConstants.DEPT_NORMAL);
            todoDept.setDelFlag("0");
            deptList.add(todoDept);
        }

        trees = getTrees(deptList, false, null);
        return trees;
    }

    /**
     * 根据角色ID查询部门（数据权限）
     *
     * @param role 角色对象
     * @return 部门列表（数据权限）
     */
    @Override
    public List<Map<String, Object>> roleDeptTreeData(SysRole role)
    {
        Long roleId = role.getRoleId();
        List<Map<String, Object>> trees = new ArrayList<Map<String, Object>>();
        List<SysDept> deptList = selectDeptList(new SysDept());
        if (StringUtils.isNotNull(roleId))
        {
            List<String> roleDeptList = deptMapper.selectRoleDeptTree(roleId);
            trees = getTrees(deptList, true, roleDeptList);
        }
        else
        {
            trees = getTrees(deptList, false, null);
        }
        return trees;
    }

    /**
     * 对象转部门树
     *
     * @param deptList 部门列表
     * @param isCheck 是否需要选中
     * @param roleDeptList 角色已存在菜单列表
     * @return
     */
    public List<Map<String, Object>> getTrees(List<SysDept> deptList, boolean isCheck, List<String> roleDeptList)
    {

        List<Map<String, Object>> trees = new ArrayList<Map<String, Object>>();
        for (SysDept dept : deptList)
        {
            if (UserConstants.DEPT_NORMAL.equals(dept.getStatus()))
            {
                Map<String, Object> deptMap = new HashMap<String, Object>();
                deptMap.put("id", dept.getDeptId());
                deptMap.put("pId", dept.getParentId());
                deptMap.put("name", dept.getDeptName());
                deptMap.put("title", dept.getDeptName());
                if (isCheck)
                {
                    deptMap.put("checked", roleDeptList.contains(dept.getDeptId() + dept.getDeptName()));
                }
                else
                {
                    deptMap.put("checked", false);
                }

                //TODO 判断是否为最后一级 此处未完美解决
                if(dept.getAncestors().split(",").length>=3) {
                    deptMap.put("type", "");
                } else {
                    deptMap.put("type", "optgroup");
                }
                trees.add(deptMap);
            }
        }
        return trees;
    }

    /**
     * 查询部门人数
     * 
     * @param parentId 部门ID
     * @return 结果
     */
    @Override
    public int selectDeptCount(String parentId)
    {
        SysDept dept = new SysDept();
        dept.setParentId(parentId);
        return deptMapper.selectDeptCount(dept);
    }

    /**
     * 查询部门是否存在用户
     * 
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(String deptId)
    {
        int result = deptMapper.checkDeptExistUser(deptId);
        return result > 0 ? true : false;
    }

    /**
     * 删除部门管理信息
     * 
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public int deleteDeptById(String deptId)
    {
        return deptMapper.deleteDeptById(deptId);
    }

    /**
     * 新增保存部门信息
     * 
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public int insertDept(SysDept dept)
    {
        // 设置id
        dept.setDeptId(IdUtil.simpleUUID());
        // 查找父节点
        SysDept info = deptMapper.selectDeptById(dept.getParentId());
        // 如果父节点不为"正常"状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus()))
        {
            throw new BusinessException("部门停用，不允许新增");
        }
        // 设置祖先列表
        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        // 设置组织架构（忽略顶级部门）
        dept.setOrgStructure("100".equals(dept.getParentId()) ? dept.getDeptName() : info.getOrgStructure() + "/" + dept.getDeptName());
        return deptMapper.insertDept(dept);
    }

    /**
     * 修改保存部门信息
     * 
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public int updateDept(SysDept dept)
    {
        // 查询父节点
        SysDept info = deptMapper.selectDeptById(dept.getParentId());
        // 父节点不为空
        if (StringUtils.isNotNull(info))
        {
            // 更新该节点的祖先列表
            String ancestors = info.getAncestors() + "," + info.getDeptId();
            dept.setAncestors(ancestors);
            // 设置组织架构（忽略顶级部门）
            String orgStructure = "100".equals(dept.getParentId()) ? dept.getDeptName() : info.getOrgStructure() + "/" + dept.getDeptName();
            dept.setOrgStructure(orgStructure);
            // 更新该节点的所有子节点
            updateDeptChildren(dept.getDeptId(), ancestors, orgStructure);
        }
        int result = deptMapper.updateDept(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus()))
        {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatus(dept);
        }
        return result;
    }

    /**
     * 修改该部门的父级部门状态
     * 
     * @param dept 当前部门
     */
    private void updateParentDeptStatus(SysDept dept)
    {
        String updateBy = dept.getUpdateBy();
        dept = deptMapper.selectDeptById(dept.getDeptId());
        dept.setUpdateBy(updateBy);
        deptMapper.updateDeptStatus(dept);
    }

    /**
     * 修改子元素关系
     * 
     * @param deptId 部门ID
     * @param ancestors 元素列表
     * @param orgStructure 组织架构
     */
    public void updateDeptChildren(String deptId, String ancestors, String orgStructure)
    {
        SysDept dept = new SysDept();
        dept.setParentId(deptId);
        List<SysDept> childrens = deptMapper.selectDeptList(dept);
        for (SysDept children : childrens)
        {
            children.setAncestors(ancestors + "," + dept.getParentId());
            children.setOrgStructure(orgStructure + "/" + children.getDeptName());
            // 递归更新子节点
            updateDeptChildren(children.getDeptId(), children.getAncestors(), children.getOrgStructure());
        }
        if (childrens.size() > 0)
        {
            deptMapper.updateDeptChildren(childrens);
        }
    }

    /**
     * 根据部门ID查询信息
     * 
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Override
    public SysDept selectDeptById(String deptId)
    {
        return deptMapper.selectDeptById(deptId);
    }

    /**
     * 校验部门名称是否唯一
     * 
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public String checkDeptNameUnique(SysDept dept)
    {
        String deptId = StringUtils.isNull(dept.getDeptId()) ? "-1" : dept.getDeptId();
        SysDept info = deptMapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId());
        if (StringUtils.isNotNull(info) && !info.getDeptId().equals(deptId))
        {
            return UserConstants.DEPT_NAME_NOT_UNIQUE;
        }
        return UserConstants.DEPT_NAME_UNIQUE;
    }


    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    @Override
    @DataScope(tableAlias = "d")
    public List<SysDept> selectTopList(SysDept dept)
    {
        return deptMapper.selectTopList(dept);
    }

    @Override
    public List<SysDept> selectListByTagCode(String tagCode) {
        return deptMapper.selectListByTagCode(tagCode);
    }

}
