package com.ruoyi.area.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.area.edu.domain.TagClient;
import com.ruoyi.area.edu.mapper.TagClientMapper;
import com.ruoyi.area.edu.service.ITagClientService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * 标签_客户端关系 服务层实现
 *
 * @author jiyunsoft
 * @date 2019-08-13
 */
@Service
public class TagClientServiceImpl extends ServiceImpl<TagClientMapper, TagClient> implements ITagClientService {
    @Override
    public List<TagClient> selectList(TagClient tagClient) {
        QueryWrapper<TagClient> query = new QueryWrapper<>();
        // 查询条件

        return list(query);
    }

    @Override
    public void deleteByTagId(String tagId) {
        QueryWrapper<TagClient> delete = new QueryWrapper<>();
        delete.lambda().eq(TagClient::getTagId, tagId);
        remove(delete);
    }
}
