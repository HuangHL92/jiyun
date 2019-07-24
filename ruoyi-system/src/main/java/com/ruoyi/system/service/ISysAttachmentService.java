package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysAttachment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 附件 服务层
 * 
 * @author jiyunsoft
 * @date 2019-02-18
 */
public interface ISysAttachmentService extends IService<SysAttachment>
{
    List<SysAttachment> selectList(SysAttachment sysAttachment);

}
