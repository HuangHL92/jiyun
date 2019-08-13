package com.ruoyi.area.edu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.area.edu.domain.Tag;

import java.util.List;

/**
 * 标签 服务层
 *
 * @author jiyunsoft
 * @date 2019-08-13
 */
public interface ITagService extends IService<Tag> {
    List<Tag> selectList(Tag tag);
}
