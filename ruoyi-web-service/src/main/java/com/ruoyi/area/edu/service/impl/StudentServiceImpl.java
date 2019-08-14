package com.ruoyi.area.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import com.ruoyi.area.edu.mapper.StudentMapper;
import com.ruoyi.area.edu.domain.Student;
import com.ruoyi.area.edu.service.IStudentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * 学生 服务层实现
 *
 * @author jiyunsoft
 * @date 2019-08-14
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {
    @Override
    public List<Student> selectList(Student student) {
        QueryWrapper<Student> query = new QueryWrapper<>();
        // 查询条件

        return list(query);
    }
}
