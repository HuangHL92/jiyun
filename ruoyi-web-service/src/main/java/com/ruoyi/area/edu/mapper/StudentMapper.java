package com.ruoyi.area.edu.mapper;

import com.ruoyi.area.edu.domain.Student;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 学生 数据层
 *
 * @author jiyunsoft
 * @date 2019-08-14
 */
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 查询列表数据
     * @param student
     * @return
     */
    List<Student> getList(Student student);
}