package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysAttachmentMapper;
import com.ruoyi.system.domain.SysAttachment;
import com.ruoyi.system.service.ISysAttachmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * 附件 服务层实现
 * 
 * @author jiyunsoft
 * @date 2019-02-18
 */
@Service
public class SysAttachmentServiceImpl extends ServiceImpl<SysAttachmentMapper, SysAttachment> implements ISysAttachmentService 
{
    @Override
    public List<SysAttachment> selectList(SysAttachment sysAttachment) {
        QueryWrapper<SysAttachment> query = new QueryWrapper<>();
        // 查询条件
        //attachmentNo
        if(StringUtils.isNotEmpty(sysAttachment.getAttachmentNo())) {
            query.lambda().eq(StrUtil.isNotBlank(sysAttachment.getAttachmentNo()), SysAttachment::getAttachmentNo, sysAttachment.getAttachmentNo());
        }


        // 关键字模糊查询
        String keyword = sysAttachment.getParams().isEmpty() ? null : sysAttachment.getParams().get("keyword").toString();
        if(StringUtils.isNotEmpty(keyword)) {
            query.lambda().and(
                    StrUtil.isNotBlank(keyword),
                    i -> i.like(SysAttachment::getFileName, keyword)

            );
        }

        return list(query);
    }


}
