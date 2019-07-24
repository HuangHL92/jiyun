package com.ruoyi.web.controller.system;

import java.util.Arrays;
import java.util.List;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.utils.workday.WorkdayUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysCalendar;
import com.ruoyi.system.service.ISysCalendarService;
import com.ruoyi.framework.web.base.BaseController;
import com.ruoyi.common.page.TableDataInfo;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.support.Convert;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 日历 信息操作处理
 *
 * @author jiyunsoft
 * @date 2019-03-09
 */
@Controller
@RequestMapping("/system/calendar")
public class SysCalendarController extends BaseController
{
    private String prefix = "system/calendar";

	@Autowired
	private ISysCalendarService sysCalendarService;

	@RequiresPermissions("system:calendar:view")
	@GetMapping()
	public String sysCalendar()
	{
	    return prefix + "/list";
	}

	/**
	 * 查询日历列表
	 */
	@RequiresPermissions("system:calendar:list")
	@PostMapping("/list")
	@ResponseBody
	public TableDataInfo list(SysCalendar sysCalendar)
	{
		startPage();
		return getDataTable(sysCalendarService.selectList(sysCalendar));
	}


	/**
	 * 导出日历列表
	 */
	@RequiresPermissions("system:calendar:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SysCalendar sysCalendar)
    {
    	List<SysCalendar> list = sysCalendarService.selectList(sysCalendar);
        ExcelUtil<SysCalendar> util = new ExcelUtil<SysCalendar>(SysCalendar.class);
        return util.exportExcel(list, "日历数据");
    }

	/**
	 * 导入日历
	 * @param file
	 * @param
	 * @return
	 * @throws Exception
	 */
	@Log(title = "日历管理", businessType = BusinessType.IMPORT)
	@RequiresPermissions("system:calendar:import")
	@PostMapping("/importData")
	@ResponseBody
	public AjaxResult importData(MultipartFile file) throws Exception
	{
		ExcelUtil<SysCalendar> util = new ExcelUtil<SysCalendar>(SysCalendar.class);
		List<SysCalendar> calendarList = util.importExcel(file.getInputStream());
		String operName = String.valueOf(getSysUser().getUserId());
		String message = sysCalendarService.importCalendar(calendarList, operName);
		return AjaxResult.success(message);
	}

	/**
	 * 下载模板
	 * @return
	 */
	@RequiresPermissions("system:calendar:view")
	@GetMapping("/importTemplate")
	@ResponseBody
	public AjaxResult importTemplate()
	{
		ExcelUtil<SysCalendar> util = new ExcelUtil<SysCalendar>(SysCalendar.class);
		return util.importTemplateExcel("日历数据");
	}
	/**
	 * 新增日历
	 */
	@GetMapping("/add")
	public String add(ModelMap mmap)
	{
        SysCalendar sysCalendar  = new SysCalendar();
        sysCalendar.setFormAction(prefix + "/add");
		mmap.put("sysCalendar", sysCalendar);
	    return prefix + "/add";
	}

	/**
	 * 新增保存日历
	 */
	@RequiresPermissions("system:calendar:add")
	@Log(title = "日历", businessType = BusinessType.INSERT)
	@PostMapping("/add")
	@ResponseBody
	public AjaxResult addSave(SysCalendar sysCalendar,HttpServletRequest request, Model model)
	{
		//设置年份,日期
		sysCalendar.setDays(WorkdayUtils.Date2Int(sysCalendar.getDateStr()));
		sysCalendar.setYears(DateUtil.year(sysCalendar.getDateStr()));

		return toAjax(sysCalendarService.saveSyscanlendar(sysCalendar));
	}
	/**
	 * 修改日历
	 */
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable("id") String id, ModelMap mmap){
		SysCalendar sysCalendar = sysCalendarService.getById(id);
		sysCalendar.setDateStr(WorkdayUtils.Integer2Date(sysCalendar.getDays()));

        sysCalendar.setFormAction(prefix + "/edit");
		mmap.put("sysCalendar", sysCalendar);
	    return prefix + "/add";
	}

	/**
	 * 修改保存日历
	 */
	@RequiresPermissions("system:calendar:edit")
	@Log(title = "日历", businessType = BusinessType.UPDATE)
	@PostMapping("/edit")
	@ResponseBody
	public AjaxResult editSave(SysCalendar sysCalendar)
	{
		//设置年份,日期
		sysCalendar.setDays(WorkdayUtils.Date2Int(sysCalendar.getDateStr()));
		sysCalendar.setYears(DateUtil.year(sysCalendar.getDateStr()));

		return toAjax(sysCalendarService.saveOrUpdate(sysCalendar));
	}

	/**
	 * 删除日历
	 */
	@RequiresPermissions("system:calendar:remove")
	@Log(title = "日历", businessType = BusinessType.DELETE)
	@PostMapping( "/remove")
	@ResponseBody
	public AjaxResult remove(String ids)
	{
		return toAjax(sysCalendarService.removeByIds(Arrays.asList(Convert.toStrArray(ids))));
	}

}
