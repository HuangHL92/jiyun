package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.util.Date;

/**
 * 附件表 sys_attachment
 * 
 * @author jiyunsoft
 * @date 2019-02-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_attachment")
public class SysAttachment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

	/**  */
    @TableId
	private String id;
	/**  */
	private String fileName;
	/**  */
	private String path;
	/**  */
	private String attachmentNo;

	private Double fileSize;
}
