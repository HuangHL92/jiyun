package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysCalendar;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 日历 数据层
 * 
 * @author jiyunsoft
 * @date 2019-03-09
 */
public interface SysCalendarMapper extends BaseMapper<SysCalendar>
{
    /**
     * 插入日历
     * @param sysCalendar
     * @return
     */
    int insertCalendar(SysCalendar sysCalendar);

    /**
     * 根据日期查询日历
     * @param days
     * @return
     */
    SysCalendar selectCalendarByDays(Integer days);

    /**
     * 更新日历
     * @param calendar
     * @return
     */
    int updateCalendar(SysCalendar calendar);
}