package com.ruoyi.area.edu.service;

import com.ruoyi.area.edu.domain.Student;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.base.AjaxResult;

import java.util.List;

/**
 * 学生 服务层
 *
 * @author jiyunsoft
 * @date 2019-08-14
 */
public interface IStudentService extends IService<Student> {
    List<Student> selectList(Student student);

    /**
     * 导入
     * @param studentList
     * @param updateSupport 是否更新支持，如果已存在，则进行更新数据
     * @return
     */
    String importData(List<Student> studentList, boolean updateSupport);

    /**
     * 根据学号查询学生
     * @param sno
     * @return
     */
    Student getBySno(String sno);

    /**
     * 校验学号是否唯一
     * @param student
     * @return
     */
    String checkSnoUnique(Student student);

    /**
     * 学生保存操作
     * @param student
     */
    AjaxResult doSave(Student student);
}
