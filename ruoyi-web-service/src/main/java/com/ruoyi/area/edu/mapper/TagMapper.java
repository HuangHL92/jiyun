package com.ruoyi.area.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.area.edu.domain.Tag;

import java.util.List;


/**
 * 标签 数据层
 *
 * @author jiyunsoft
 * @date 2019-08-13
 */
public interface TagMapper extends BaseMapper<Tag> {

    Tag selectById(String id);

    List<Tag> selectList(Tag tag);
}