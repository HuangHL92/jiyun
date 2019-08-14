package com.ruoyi.area.edu.service;

import com.ruoyi.area.edu.domain.Student;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 学生 服务层
 *
 * @author jiyunsoft
 * @date 2019-08-14
 */
public interface IStudentService extends IService<Student> {
    List<Student> selectList(Student student);
}
