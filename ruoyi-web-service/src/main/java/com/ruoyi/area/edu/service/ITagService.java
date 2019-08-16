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
    /**
     * 列表查询
     * @param tag
     * @return
     */
    List<Tag> getList(Tag tag);

    /**
     * 根据id获取对象
     * @param id
     * @return
     */
    Tag selectById(String id);

    /**
     * 保存操作
     * @param tag
     * @return
     */
    void doSave(Tag tag);

    /**
     * 校验编码
     * @param tag
     * @return
     */
    String checkCodeUnique(Tag tag);
}
