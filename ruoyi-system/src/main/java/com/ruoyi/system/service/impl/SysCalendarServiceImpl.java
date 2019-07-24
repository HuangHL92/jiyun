package com.ruoyi.system.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.workday.WorkdayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysCalendarMapper;
import com.ruoyi.system.domain.SysCalendar;
import com.ruoyi.system.service.ISysCalendarService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * 日历 服务层实现
 *
 * @author jiyunsoft
 * @date 2019-03-09
 */
@Service
public class SysCalendarServiceImpl extends ServiceImpl<SysCalendarMapper, SysCalendar> implements ISysCalendarService {
    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysCalendarMapper calendarMapper;


    /**
     * 查询列表
     * @param sysCalendar
     * @return
     */
    @Override
    public List<SysCalendar> selectList(SysCalendar sysCalendar) {
        QueryWrapper<SysCalendar> query = new QueryWrapper<>();
        // 查询条件
        // 关键字模糊查询（年度/日期）
        query.lambda().eq(sysCalendar.getYears()!=null, SysCalendar::getYears, sysCalendar.getYears());

        List<SysCalendar> calendars = list(query);
        for (SysCalendar calendar : calendars){
            calendar.setDateStr(DateUtil.parse(calendar.getDays().toString()));
        }

        return calendars;
    }

    /**
     * 导入日历数据
     *
     * @param calendarList
     * @param
     * @param operName
     * @return
     */
    @Override
    public String importCalendar(List<SysCalendar> calendarList, String operName) {
        if (StringUtils.isNull(calendarList) || calendarList.size() == 0) {
            throw new BusinessException("导入日历数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        //循环遍历list进行插入或更新
        for (SysCalendar calendar : calendarList) {

            String dateStr = WorkdayUtils.date2String(calendar.getDateStr()).substring(0,10);

            try {
                // 验证是否存在这个日历
                SysCalendar sysCalendar = calendarMapper.selectCalendarByDays(Integer.valueOf(WorkdayUtils.Date2Int(calendar.getDateStr())));
                if (StringUtils.isNull(sysCalendar)) {
                    //设置日期类型,年份,创建人
                    calendar.setDayType(Integer.valueOf(calendar.getType()));
                    calendar.setYears(Integer.valueOf(DateUtil.format(calendar.getDateStr(),"yyyy")));
                    calendar.setDays(Integer.valueOf(WorkdayUtils.Date2Int(calendar.getDateStr())));
                    calendar.setCreateBy(operName);

                    baseMapper.insert(calendar);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、日历 " + dateStr + " 导入成功");
                } else {
                    //设置日期类型,年份,创建人
                    sysCalendar.setDayType(Integer.valueOf(calendar.getType()));
                    sysCalendar.setUpdateBy(operName);
                    this.updateCalendar(sysCalendar);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、日历 " + dateStr + " 更新成功");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、日历 " + dateStr + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new BusinessException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    /**
     * 添加日历
     * @param sysCalendar
     * @return
     */
    @Override
    public int saveSyscanlendar(SysCalendar sysCalendar) {

        SysCalendar calendar = calendarMapper.selectCalendarByDays(sysCalendar.getDays());
        //如果id不为空,则更新
        if (StringUtils.isNotEmpty(calendar.getId())){
            sysCalendar.setId(calendar.getId());
            return calendarMapper.updateCalendar(sysCalendar);
        }
        return calendarMapper.insertCalendar(sysCalendar);
    }

    /**
     * 更新日历数据
     *
     * @param calendar
     * @return
     */
    private int updateCalendar(SysCalendar calendar) {

        return calendarMapper.updateCalendar(calendar);

    }

    /**
     * 插入日历数据
     *
     * @param sysCalendar
     * @return
     */
    private int insertCalendar(SysCalendar sysCalendar) {

        // 新增日历数据
        int rows = calendarMapper.insertCalendar(sysCalendar);

        return rows;
    }
}
