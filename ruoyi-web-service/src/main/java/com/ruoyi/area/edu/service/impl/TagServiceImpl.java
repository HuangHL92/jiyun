package com.ruoyi.area.edu.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.area.edu.domain.Tag;
import com.ruoyi.area.edu.mapper.TagMapper;
import com.ruoyi.area.edu.service.ITagService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 标签 服务层实现
 *
 * @author jiyunsoft
 * @date 2019-08-13
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements ITagService {
    @Override
    public List<Tag> selectList(Tag tag) {
        // 查询条件构造
        QueryWrapper<Tag> query = new QueryWrapper<>();
        // 1.标签类型
        query.lambda().eq(StrUtil.isNotEmpty(tag.getType()), Tag::getType, tag.getType());
        // 2.状态
        query.lambda().eq(StrUtil.isNotEmpty(tag.getStatus()), Tag::getStatus, tag.getStatus());
        // 3.关键字：标签名称/关键字
        String keyword = tag.getParams().isEmpty() ? null : tag.getParams().get("keyword").toString();
        query.lambda().and(StrUtil.isNotBlank(keyword), i -> i.like(Tag::getName, keyword)
                .or().like(Tag::getRemark, keyword));

        return list(query);
    }
}
