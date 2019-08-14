package com.ruoyi.area.edu.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.area.edu.domain.Tag;
import com.ruoyi.area.edu.domain.TagClient;
import com.ruoyi.area.edu.mapper.TagMapper;
import com.ruoyi.area.edu.service.ITagClientService;
import com.ruoyi.area.edu.service.ITagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 标签 服务层实现
 *
 * @author jiyunsoft
 * @date 2019-08-13
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {

    @Autowired
    private ITagClientService tagClientService;

    @Override
    public List<Tag> selectList(Tag tag) {
        return baseMapper.selectList(tag);
    }

    @Override
    public Tag selectById(String id) {
        return baseMapper.selectById(id);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doSave(Tag tag) {
        // 1.标签表数据维护
        if (StrUtil.isEmpty(tag.getId())) { // 新增
            save(tag);
        } else { // 修改
            updateById(tag);
        }
        // 2.标签_客户端关系表维护
        String oldClientIds = tag.getOldClientIds();
        String clientIds = tag.getClientIds();
        if (StrUtil.isNotEmpty(oldClientIds)) {
            // 删除原有数据
            tagClientService.deleteByTagId(tag.getId());
        }
        if (StrUtil.isNotEmpty(clientIds) && !tag.isClientNull()) {
            // 新增数据
            List<TagClient> tagClientList = new ArrayList<>();
            for (String clientId : clientIds.split(",")) {
                if (StrUtil.isEmpty(clientId))
                    continue;
                TagClient tagClient = new TagClient();
                tagClient.setClientId(clientId);
                tagClient.setTagId(tag.getId());
                tagClientList.add(tagClient);
            }
            tagClientService.saveBatch(tagClientList);
        }
    }
}
