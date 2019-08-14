package com.ruoyi.area.edu.service;

import com.ruoyi.area.edu.domain.TagClient;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 标签_客户端关系 服务层
 *
 * @author jiyunsoft
 * @date 2019-08-13
 */
public interface ITagClientService extends IService<TagClient> {
    List<TagClient> selectList(TagClient tagClient);

    /**
     * 根据标签id删除数据
     * @param tagId
     */
    void deleteByTagId(String tagId);
}
