package com.ruoyi.web.controller.system;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysUser;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysAttachment;
import com.ruoyi.system.service.ISysAttachmentService;
import com.ruoyi.framework.web.base.BaseController;
import com.ruoyi.common.page.TableDataInfo;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.support.Convert;

/**
 * 附件 信息操作处理
 * 
 * @author jiyunsoft
 * @date 2019-02-18
 */
@Controller
@RequestMapping("/system/attachment")
public class SysAttachmentController extends BaseController
{
    private String prefix = "system/attachment";
	
	@Autowired
	private ISysAttachmentService sysAttachmentService;
	
	@RequiresPermissions("system:attachment:view")
	@GetMapping()
	public String sysAttachment()
	{
	    return prefix + "/list";
	}
	
	/**
	 * 查询附件列表
	 */
	@RequiresPermissions("system:attachment:list")
	@PostMapping("/list")
	@ResponseBody
	public TableDataInfo list(SysAttachment sysAttachment)
	{
		startPage();
		return getDataTable(sysAttachmentService.selectList(sysAttachment));
	}


	/**
	 * 导出附件列表
	 */
	@RequiresPermissions("system:attachment:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SysAttachment sysAttachment)
    {
    	List<SysAttachment> list = sysAttachmentService.selectList(sysAttachment);
        ExcelUtil<SysAttachment> util = new ExcelUtil<SysAttachment>(SysAttachment.class);
        return util.exportExcel(list, "sysAttachment");
    }

	/**
	 * 新增附件
	 */
	@GetMapping("/add")
	public String add()
	{
	    return prefix + "/add";
	}
	
	/**
	 * 新增保存附件
	 */
	@RequiresPermissions("system:attachment:add")
	@Log(title = "附件", businessType = BusinessType.INSERT)
	@PostMapping("/add")
	@ResponseBody
	public AjaxResult addSave(SysAttachment sysAttachment)
	{		
		return toAjax(sysAttachmentService.save(sysAttachment));
	}

	/**
	 * 修改附件
	 */
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable("id") String id, ModelMap mmap)
	{
		SysAttachment sysAttachment = sysAttachmentService.getById(id);
		mmap.put("sysAttachment", sysAttachment);
	    return prefix + "/edit";
	}
	
	/**
	 * 修改保存附件
	 */
	@RequiresPermissions("system:attachment:edit")
	@Log(title = "附件", businessType = BusinessType.UPDATE)
	@PostMapping("/edit")
	@ResponseBody
	public AjaxResult editSave(SysAttachment sysAttachment)
	{		
		return toAjax(sysAttachmentService.updateById(sysAttachment));
	}
	
	/**
	 * 删除附件
	 */
	@RequiresPermissions("system:attachment:remove")
	@Log(title = "附件", businessType = BusinessType.DELETE)
	@PostMapping( "/remove")
	@ResponseBody
	public AjaxResult remove(String ids)
	{		
		return toAjax(sysAttachmentService.removeByIds(Arrays.asList(Convert.toStrArray(ids))));
	}
	
}
