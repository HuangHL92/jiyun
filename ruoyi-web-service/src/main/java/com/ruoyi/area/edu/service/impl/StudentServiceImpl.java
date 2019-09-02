package com.ruoyi.area.edu.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.area.edu.domain.Tag;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.Md5Utils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.area.edu.mapper.StudentMapper;
import com.ruoyi.area.edu.domain.Student;
import com.ruoyi.area.edu.service.IStudentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学生 服务层实现
 *
 * @author jiyunsoft
 * @date 2019-08-14
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Autowired
    private ISysConfigService configService;
    @Autowired
    private ISysDeptService deptService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private ISysRoleService roleService;

    @Override
    public List<Student> selectList(Student student) {
        return baseMapper.getList(student);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importData(List<Student> studentList, boolean updateSupport) {
        if (StringUtils.isNull(studentList) || studentList.size() == 0) {
            throw new BusinessException("导入学生数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        Student student;

        // 1.数据验证：学号、姓名、学校、年级、班级、账号状态 必填
        for (int index = 0; index < studentList.size(); index++) {
            student = studentList.get(index);
            if (StrUtil.isBlank(student.getSno())
                    || StrUtil.isBlank(student.getName())
                    || StrUtil.isBlank(student.getDeptId())
                    || StrUtil.isBlank(student.getGrade())
                    || student.getClassStr() == null
                    || StrUtil.isBlank(student.getStatus())) {
                failureMsg.append("第" + (index + 2) + "行数据存在异常<br/>");
                failureNum++;
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，共" + failureNum + "条数据存在异常<br>" +
                    "原因：[学号]、[姓名]、[学校]、[年级]、[班级]、[账号状态]字段必填；[年级]请输入数字<br>");
            throw new BusinessException(failureMsg.toString());
        }

        // 2.判断导入数据中是否有重复的学号（避免下面插入数据需要回滚，由于还涉及用户表和用户缓存，所以提前校验）
        failureNum = 0;
        failureMsg = new StringBuilder();
        List<String> snoList=  studentList.stream().map(Student::getSno).collect(Collectors.toList());
        Map<String, Long> map = snoList.stream().collect(Collectors.groupingBy(p -> p, Collectors.counting()));
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "：" + entry.getValue());
            if (entry.getValue() > 1) {
                failureNum += entry.getValue();
                failureMsg.append("学号[" + entry.getKey() + "]数据存在异常<br/>");
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，以下学号的数据存在不止一条<br/>");
            throw new BusinessException(failureMsg.toString());
        }

        // 3.根据学校名称，找到学校id TODO 管理员可操作的数据范围
        // 3.1 查询所有学校的数据
        List<SysDept> schoolList = deptService.selectListByTagCode(Tag.TAG_CODE_JGLX_XX);
        Map<String, String> deptMaps = schoolList.stream().collect(Collectors.toMap(SysDept::getDeptName, SysDept::getDeptId));
        failureNum = 0;
        failureMsg = new StringBuilder();
        for (int index = 0; index < studentList.size(); index++) {
            student = studentList.get(index);
            String deptId = deptMaps.get(student.getDeptId()); // 此处导入是把学校存入deptId属性
            if (StrUtil.isEmpty(deptId)) {
                failureMsg.append("第" + (index + 2) + "行数据存在异常<br/>");
                failureNum++;
            } else {
                studentList.get(index).setDeptId(deptId);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，共" + failureNum + "条数据存在异常<br>" +
                    "原因：根据学校名称无法找到对应的学校<br>");
            throw new BusinessException(failureMsg.toString());
        }

        // 4.根据更新标识，校验是否有存在重复数据（根据学号校验）
        failureNum = 0;
        failureMsg = new StringBuilder();
        if (!updateSupport) {
            for (int index = 0; index < studentList.size(); index++) {
                student = studentList.get(index);
                // 判断学生是否存在
                if (getBySno(student.getSno()) != null) {
                    failureMsg.append("第" + (index + 2) + "行数据存在异常<br/>");
                    failureNum++;
                }
            }
            if (failureNum > 0) {
                failureMsg.insert(0, "很抱歉，共" + failureNum + "条数据存在异常<br>" +
                        "原因：该学号的学生已存在，如果需要更新，请勾选更新按钮<br>");
                throw new BusinessException(failureMsg.toString());
            }
        }

        // 5.学生&用户数据导入：此处无法使用batch操作
        //   用户初始密码
        Student target;
        SysUser user;
        String password = configService.selectConfigByKey("sys.user.initPassword");
        // 查询出[学生]角色
        Long studentRoleId = null;
        List<SysRole> roles = roleService.selectRoleAll();
        for (SysRole role : roles) {
            if (role.getRoleKey().equals("student")) {
                studentRoleId = role.getRoleId();
            }
        }
        Long[] roleIds = new Long[]{ studentRoleId };
        for (int index = 0; index < studentList.size(); index++) {
            try {
                student = studentList.get(index);
                // 判断学生数据是否存在
                target = getBySno(student.getSno());
                if (target == null) {
                    // 插入用户数据
                    user = new SysUser();
                    user.setLoginName(student.getSno()); // 登录名称
                    user.setDeptId(student.getDeptId()); // 单位/部门
                    user.setUserName(student.getName()); // 用户名称
                    user.setStatus(student.getStatus()); // 状态
                    user.setPassword(Md5Utils.hash(user.getLoginName() + password)); // 密码
                    user.setRoleIds(roleIds); // 角色
                    userService.insertUser(user);

                    // 插入学生数据
                    student.setUserId(user.getUserId()); // 用户id
                    save(student);
                } else {
                    // 更新用户数据
                    user = userService.selectUserById(target.getUserId());
                    user.setLoginName(student.getSno()); // 登录名称
                    user.setDeptId(student.getDeptId()); // 单位/部门
                    user.setUserName(student.getName()); // 用户名称
                    user.setStatus(student.getStatus()); // 状态
                    userService.updateUser(user);

                    // 更新学生数据
                    student.setId(target.getId());
                    updateById(student);
                }
                successNum++;
                successMsg.append("第" + (index + 2) + "行数据导入成功<br/>");
            } catch (Exception e) {
                failureNum++;
                String msg = "第" + (index + 2) + "行数据导入失败：" + e.getMessage() + "<br/>";
                failureMsg.append(msg);
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据导入失败，错误如下：<br/>");
            throw new BusinessException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：<br/>");
        }
        return successMsg.toString();
    }

    @Override
    public Student getBySno(String sno) {
        if (StrUtil.isEmpty(sno)) {
            return null;
        }
        QueryWrapper<Student> query = new QueryWrapper();
        query.lambda().eq(Student::getSno, sno);
        return getOne(query);
    }
}
