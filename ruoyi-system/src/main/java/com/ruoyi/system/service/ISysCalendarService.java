package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysCalendar;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 日历 服务层
 * 
 * @author jiyunsoft
 * @date 2019-03-09
 */
public interface ISysCalendarService extends IService<SysCalendar>
{
    /**
     * 查询列表
     * @param sysCalendar
     * @return
     */
    List<SysCalendar> selectList(SysCalendar sysCalendar);

    /**
     * 导入数据
     * @param calendarList
     * @param
     * @param operName
     * @return
     */
    String importCalendar(List<SysCalendar> calendarList, String operName);

    /**
     * 添加日历
     * @param sysCalendar
     * @return
     */
    int saveSyscanlendar(SysCalendar sysCalendar);
}
