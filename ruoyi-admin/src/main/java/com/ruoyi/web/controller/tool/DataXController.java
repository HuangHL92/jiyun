package com.ruoyi.web.controller.tool;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.enums.DataXSqlType;
import com.ruoyi.common.page.TableDataInfo;
import com.ruoyi.common.support.Convert;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.web.base.BaseController;
import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.service.ISysJobService;
import com.ruoyi.system.common.DataXJsonCommon;
import com.ruoyi.system.domain.SysDataX;
import com.ruoyi.system.service.ISysDataXService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.*;

/**
 * Datax配置 信息操作处理
 *
 * @author jiyunsoft
 * @date 2019-04-02
 */
@Controller
@RequestMapping("/tool/dataX")
public class DataXController extends BaseController {
    private String prefix = "tool/dataX";
    private static final String SYS_JOB_NAME = "sysDataxTask";
    private static final String SYS_JOB_METHOD_NAME = "sysDataxParams";
    private static final String SYS_JOB_AFTER_NAME = "_Datax";
    @Autowired
    private ISysDataXService sysDataXService;
    @Autowired
    private ISysJobService sysJobService;

    @RequiresPermissions("tool:dataX:view")
    @GetMapping()
    public String dataX() {
        return prefix + "/list";
    }

    /**
     * 查询Datax配置列表
     */
    @RequiresPermissions("tool:dataX:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysDataX sysDataX) {
        startPage();
        return getDataTable(sysDataXService.selectList(sysDataX));
    }


    /**
     * 导出Datax配置列表
     */
    @RequiresPermissions("tool:dataX:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SysDataX sysDataX) {
        List<SysDataX> list = sysDataXService.selectList(sysDataX);
        ExcelUtil<SysDataX> util = new ExcelUtil<SysDataX>(SysDataX.class);
        return util.exportExcel(list, "sysDataX");
    }

    /**
     * 新增Datax配置
     */
    @GetMapping("/add")
    public String add(ModelMap mmap) {
        SysDataX sysDataX = new SysDataX();
        //表单Action指定
        sysDataX.setFormAction(prefix + "/add");
        mmap.put("sysDataX", sysDataX);
        SysJob sysJob = new SysJob();
        sysJob.setJobName(SYS_JOB_NAME);
        List<SysJob> sysJobs = sysJobService.selectJobList(sysJob);
        mmap.put("sysJobs", sysJobs);
        return prefix + "/add";
    }

    /**
     * 新增保存Datax配置
     */
    @RequiresPermissions("tool:dataX:add")
    @Log(title = "Datax配置", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(SysDataX sysDataX, HttpServletRequest request, Model model) {
        if (StringUtils.isNotBlank(sysDataX.getId())) {
            SysDataX sysDataXOne = sysDataXService.getById(sysDataX);
            if (sysDataXOne.getIsSchedule().equals(SysDataX.DATAX_SCHEDULE_YES)) {
                //1 有任务
                //1.1 查找任务
                try {
                    SysJob job = sysJobService.selectJobById(Long.parseLong(sysDataXOne.getScheduleId()));

                    if (job != null && sysDataX.getIsSchedule().equals(SysDataX.DATAX_SCHEDULE_NO)) {
                        //1.2取消任务
                        //删除任务
                        delSysJob(sysDataXOne.getScheduleId(), sysDataXOne.getFileName());
                        //更新datax的值
                        sysDataX.setScheduleCron("");
                        sysDataX.setScheduleName("");
                    } else if (null == job && sysDataX.getIsSchedule().equals(SysDataX.DATAX_SCHEDULE_YES)) {
                        //1.3添加新任务
                        addSysJob(sysDataX);
                    } else if (job != null && sysDataX.getIsSchedule().equals(SysDataX.DATAX_SCHEDULE_YES)) {
                        //1.4切换至其他任务
                        //1.4.1删除旧任务
                        delSysJob(sysDataXOne.getScheduleId(), sysDataXOne.getFileName());
                        //1.4.2新增新任务
                        addSysJob(sysDataX);
                    }
                } catch (NumberFormatException e) {
                    DataXJsonCommon.dataxJsonMod(sysDataX);
                    sysDataX.setLog("");
                }
            } else {
                //2 无任务
                //2.1添加到新任务
                addSysJob(sysDataX);
            }
            //生成json文件
            DataXJsonCommon.dataxJsonMod(sysDataX);
            sysDataX.setLog("");
            return toAjax(sysDataXService.saveOrUpdate(sysDataX));
        } else {
            //是否要加入定时任务
            if (sysDataX.getIsSchedule().equals(SysDataX.DATAX_SCHEDULE_YES)) {
                sysDataX.setId(IdUtil.simpleUUID());
                //如果为新增定时任务
                addSysJob(sysDataX);
            }
            //保存数据
            sysDataXService.save(sysDataX);

            //生成json文件
            boolean res = DataXJsonCommon.dataxJsonMod(sysDataX);
            if (!res) {
                return error("配置文件生成失败！请检查配置。");
            }
            return toAjax(res);

        }

    }

    /**
     * 修改Datax配置
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, ModelMap mmap) {
        SysDataX sysDataX = sysDataXService.getById(id);
        //表单Action指定
        sysDataX.setFormAction(prefix + "/edit");
        //主键加密（配合editSave方法使用）- 如果需防止数据ID泄露，请放开，否则请删除此处代码
        //sysDataX.setId(pk_encrypt(sysDataX.getId()));

        mmap.put("sysDataX", sysDataX);
        SysJob sysJob = new SysJob();
        sysJob.setJobName(SYS_JOB_NAME);
        List<SysJob> sysJobs = sysJobService.selectJobList(sysJob);
        mmap.put("sysJobs", sysJobs);
        return prefix + "/add";
    }

    /**
     * 修改保存Datax配置
     */
    @RequiresPermissions("tool:dataX:edit")
    @Log(title = "Datax配置", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(SysDataX sysDataX) {

        //主键解密（配合edit方法使用，请确认edit方法中加密了）- 如果需防止数据ID泄露，请放开，否则请删除此处代码
        //sysDataX.setId(pk_decrypt(sysDataX.getId()));

        return toAjax(sysDataXService.saveOrUpdate(sysDataX));
    }

    /**
     * 删除Datax配置
     */
    @RequiresPermissions("tool:dataX:remove")
    @Log(title = "Datax配置", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {

        //删除对应的文件
        for (String id : Arrays.asList(ids.split(","))
        ) {
            SysDataX sysDataX = sysDataXService.getById(id);
            //删除定时任务 捕获转换异常
            try {
                delSysJob(sysDataX.getScheduleId(), sysDataX.getFileName());
                DataXJsonCommon.delJsonAndLog(sysDataX.getFileName());
            } catch (NumberFormatException e) {
                //删除文件
                DataXJsonCommon.delJsonAndLog(sysDataX.getFileName());
            }
        }
        return toAjax(sysDataXService.removeByIds(Arrays.asList(Convert.toStrArray(ids))));


    }

    /**
     * 文件名唯一验证
     *
     * @param oldFileName
     * @param fileName
     * @return
     */
    @PostMapping("/validateFileName")
    @ResponseBody
    public String validateFileName(@PathParam("oldFileName") String oldFileName, @PathParam("fileName") String fileName) {
        if (StringUtils.isNotEmpty(oldFileName) && oldFileName.equals(fileName)) {
            return "0";
        }
        if (sysDataXService.getOne(new QueryWrapper<SysDataX>().eq("file_name", fileName)) == null
        ) {
            return "0";
        }
        return "1";
    }

    @PostMapping("/syc")
    @ResponseBody
    public AjaxResult syc(String id, String fileName) {
        if (StrUtil.isNotBlank(fileName) && StrUtil.isNotBlank(id)) {
            SysDataX sysDataX = new SysDataX();
            sysDataX.setId(id);
            String log = DataXJsonCommon.exeDataX(fileName);
            sysDataX.setLog(log);
            return toAjax(sysDataXService.saveOrUpdate(sysDataX));
        } else {
            //文件名空返回失败
            return toAjax(false);
        }
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") String id, ModelMap mmap, String showLog) {
        mmap.put("sysDataX", sysDataXService.getById(id));
        return prefix + "/detail";
    }

    /**
     * 删除任务
     *
     * @param jobId
     * @param methodParam
     */
    public void delSysJob(String jobId, String methodParam) {
        SysJob sysJob = sysJobService.selectJobById(Long.parseLong(jobId));
        if (sysJob != null) {
            List<String> methodParamsList = new ArrayList<String>();
            Collections.addAll(methodParamsList, sysJob.getMethodParams().split(","));
            if (methodParamsList.contains(methodParam)) {
                methodParamsList.remove(methodParam);
                sysJob.setMethodParams(String.join(",", methodParamsList));
                sysJobService.updateJobCron(sysJob);
            }
        }
    }

    /**
     * 增加一个新任务
     *
     * @param sysDataX
     */
    public void addSysJob(SysDataX sysDataX) {
        if (StrUtil.isBlank(sysDataX.getScheduleId())) {
            //增加一个定时任务 任务名\任务组\任务方法\任务参数(文件名、sysDataXId)\执行表达式
            SysJob sysJob = new SysJob();
            sysJob.setJobName(SYS_JOB_NAME);
            sysJob.setJobGroup(sysDataX.getFileName() + SYS_JOB_AFTER_NAME);
            sysJob.setMethodName(SYS_JOB_METHOD_NAME);
            sysJob.setMethodParams(sysDataX.getFileName());
            sysJob.setCronExpression(sysDataX.getScheduleCron());
            sysJobService.insertJobCron(sysJob);
            //变更datax值
            sysDataX.setScheduleName(sysJob.getJobGroup());
            sysDataX.setScheduleId(String.valueOf(sysJob.getJobId()));
        } else {
            //如果为加入旧的定时任务
            //根据任务id查找任务
            SysJob job = sysJobService.selectJobById(Long.parseLong(sysDataX.getScheduleId()));
            if (job != null) {
                List<String> methodParamsList = new ArrayList<String>();
                if (StrUtil.isNotBlank(job.getMethodParams())) {
                    Collections.addAll(methodParamsList, job.getMethodParams().split(","));
                }
                methodParamsList.add(sysDataX.getFileName());
                //增加参数（文件名） 修改任务
                job.setMethodParams(String.join(",", methodParamsList));
                sysJobService.updateJobCron(job);
                //变更datax值
                sysDataX.setScheduleName(job.getJobGroup());
                sysDataX.setScheduleCron(job.getCronExpression());
                sysDataX.setScheduleId(String.valueOf(job.getJobId()));
            }

        }
    }

    /**
     * 测试连接
     */
    @GetMapping("/checkConnection")
    @ResponseBody
    public AjaxResult checkConnection(String sqlType, String port,String name, String pwd){
        String url = null;
        if(String.valueOf(DataXSqlType.MYSQL.getType()).equals(sqlType)){
            url=DataXSqlType.MYSQL.getPrefix()+port+DataXSqlType.MYSQL.getSuffix();
        }else if(String.valueOf(DataXSqlType.ORACLE.getType()).equals(sqlType)){
            url=DataXSqlType.ORACLE.getPrefix()+port+DataXSqlType.ORACLE.getSuffix();
        }else if(String.valueOf(DataXSqlType.SQL_SERVER.getType()).equals(sqlType)){
            url=DataXSqlType.SQL_SERVER.getPrefix()+port+DataXSqlType.SQL_SERVER.getSuffix();
        }
        if(DataXJsonCommon.getConnection(sqlType,url,name,pwd)!=null){
            return toAjax(true);
        }
        return toAjax(false);
    }
}
