package com.ruoyi.web.controller.demo;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.*;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.word.WordExportUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.area.demo.domain.Department;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.support.Convert;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.JedisUtils;
import com.ruoyi.common.utils.PdfUtils;
import com.ruoyi.area.demo.domain.Demo;
import com.ruoyi.area.demo.service.IDemoService;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.workday.WorkdayUtils;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.system.domain.SysCalendar;
import com.ruoyi.system.service.ISysCalendarService;
import com.ruoyi.system.service.ISysPostService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.web.websocket.SocketServer;
import com.sun.jna.platform.win32.Guid;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;

import com.ruoyi.framework.web.base.BaseController;
import com.ruoyi.common.page.TableDataInfo;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static cn.hutool.extra.servlet.ServletUtil.isIE;

/**
 * 测试 信息操作处理
 *
 * @author ruoyi
 * @date 2019-01-18
 */
@Controller
@RequestMapping("/demo/all")
public class DemoController extends BaseController {
    private String prefix = "demo/all";

    @Autowired
    private IDemoService demoService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private ISysCalendarService sysCalendarService;

    @RequiresPermissions("demo:all:view")
    @GetMapping()
    public String demo() {
        return prefix + "/all";
    }

    /**
     * 查询测试列表
     */
    @RequiresPermissions("demo:all:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Demo demo) {

        startPage();
        return getDataTable(demoService.selectList(demo));
    }


    /**
     * 导出测试列表
     */
    @RequiresPermissions("demo:all:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(Demo demo) {

        PageHelper.startPage(1, 999999, "name");
        List<Demo> list = demoService.selectList(demo);
        ExcelUtil<Demo> util = new ExcelUtil<Demo>(Demo.class);
        return util.exportExcel(list, "demo");
    }

    /**
     * 新增测试
     */
    @GetMapping("/add")
    public String add(ModelMap mmap) {
        mmap.put("posts", postService.selectPostAll());
        Demo demo = new Demo();
        //表单Action指定
        demo.setFormAction(prefix + "/add");
        mmap.put("demo", demo);
        return prefix + "/add";
    }

    /**
     * 新增保存测试
     */
    @RequiresPermissions("demo:all:add")
    @Log(title = "测试", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Demo demo, HttpServletRequest request, HttpServletResponse response, Model model) {
        boolean rflag = false;

        demo.setId(Guid.GUID.newGuid().toGuidString());
        rflag = demoService.save(demo);

        //测试websocket，给页面发消息通知
        if (rflag) {
            SocketServer.sendMessage("model3", "websocketDemo");
        }

        return toAjax(rflag);

    }

    /**
     * 修改测试
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, ModelMap mmap) {

        Demo demo = demoService.getById(id);
        //主键加密（TODO：配合editSave方法使用）
        demo.setId(pk_encrypt(demo.getId()));
        //表单Action指定
        demo.setFormAction(prefix + "/edit");

        mmap.put("demo", demo);
        return prefix + "/add";
    }

    /**
     * 修改保存测试
     */
    @RequiresPermissions("demo:all:edit")
    @Log(title = "测试", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(Demo demo) {
        //主键解密（TODO：配合edit方法使用，请确认edit方法中加密了）
        demo.setId(pk_decrypt(demo.getId()));

        boolean rflag = demoService.saveOrUpdate(demo);

        //测试websocket，给页面发消息通知
        if (rflag) {
            SocketServer.sendMessage("model3", "websocketDemo");
        }
        return toAjax(rflag);
    }

    /**
     * 详情
     */
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") String id, ModelMap mmap) {
        Demo demo = demoService.getById(id);
        mmap.put("demo", demo);
        return prefix + "/detail";
    }

    /**
     * 删除测试
     */
    @RequiresPermissions("demo:all:remove")
    @Log(title = "测试", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(demoService.removeByIds(Arrays.asList(Convert.toStrArray(ids))));
    }

    /**
     * 根据两个日期查询之间的工作日
     *
     * @param startTime
     * @param endTime
     * @param request
     * @param sysCalendar
     * @return
     */
    @PostMapping("/searchWorkDayCount")
    @ResponseBody
    public AjaxResult searchWorkDayCount(String startTime, String endTime,
                                    HttpServletRequest request, SysCalendar sysCalendar) {
        HashMap<Integer, Integer> map = new HashMap<>();

        //查询开始时间与结束时间范围内的节假日工作日日历集合
        QueryWrapper<SysCalendar> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("days", Integer.parseInt(startTime.replaceAll("-", "")), Integer.parseInt(endTime.replaceAll("-", "")));
        List<SysCalendar> list = sysCalendarService.list(queryWrapper);
        //把查到的日历数据循环,key存日期,value存日期类型:节假日/工作日
        for (SysCalendar obj : list) {
            map.put(obj.getDays(), obj.getDayType());
        }
        //初始化map
        WorkdayUtils.init(map);

        return AjaxResult.success("" + WorkdayUtils.howManyWorkday(DateUtil.parseDate(startTime), DateUtil.parseDate(endTime)));

    }

    /**
     * 根据日期和工作日天数查询N天后的工作日期
     *
     * @param oldTime
     * @param num
     * @param request
     * @param sysCalendar
     * @return
     */
    @PostMapping("/searchWorkDay")
    @ResponseBody
    public AjaxResult searchWorkDay(String oldTime, String num,
                                         HttpServletRequest request, SysCalendar sysCalendar) {

        HashMap<Integer, Integer> map = new HashMap<>();

        //查询该时间后的所有节假日以及工作日集合
        QueryWrapper<SysCalendar> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("years", DateUtils.parseDateToStr("YYYY", DateUtils.parseDate(oldTime)));
        List<SysCalendar> list = sysCalendarService.list(queryWrapper);

        //把查到的日历数据循环,key存日期,value存日期类型:节假日/工作日
        for (SysCalendar obj : list) {
            map.put(obj.getDays(), obj.getDayType());
        }
        //初始化map
        WorkdayUtils.init(map);

        return AjaxResult.success("" + WorkdayUtils.getIncomeDate(DateUtil.parseDate(oldTime), Integer.valueOf(num)));

    }


    /**
     * 生成批量数据
     */
    @Log(title = "生成批量数据", businessType = BusinessType.OTHER)
    @PostMapping("/batchData")
    @ResponseBody
    public AjaxResult batchData(HttpServletRequest request) {

        List<Demo> ls = new ArrayList<Demo>();
        int i = 0;
        while (i < 5000) {
            Demo demo = new Demo();
            demo.setId(IdUtil.randomUUID());
            demo.setName("姓名" + i);
            ls.add(demo);
            i++;
        }
        demoService.saveBatch(ls);

        return AjaxResult.success("成功插入5000条记录");
    }


    /**
     * 清空表
     */
    @Log(title = "清空表", businessType = BusinessType.OTHER)
    @PostMapping("/clearAll")
    @ResponseBody
    public AjaxResult clearAll(HttpServletRequest request) {
        QueryWrapper<Demo> query = new QueryWrapper<>();
        demoService.remove(query);

        return AjaxResult.success();
    }


    /**
     * 发送邮件
     */
    @Log(title = "发送邮件", businessType = BusinessType.OTHER)
    @PostMapping("/sendMail")
    @ResponseBody
    public AjaxResult sendMail(HttpServletRequest request) {
        String mailto = StringUtils.isEmpty(request.getParameter("mailto")) ? "fei.yu@51e.com.cn" : request.getParameter("mailto");
        String mailtext = StringUtils.isEmpty(request.getParameter("mailtext")) ? "邮件来自Hutool测试" : request.getParameter("mailtext");
        MailUtil.send(mailto, "测试", mailtext, false);

        return toAjax(1);
    }


    /**
     * 生成二维码
     */
    @Log(title = "生成二维码", businessType = BusinessType.OTHER)
    @PostMapping("/createQRCode")
    @ResponseBody
    public AjaxResult createQRCode(HttpServletRequest request) {

        //自定义配置
        QrConfig config = new QrConfig(300, 300);
        // 设置边距，既二维码和背景之间的边距
        config.setMargin(3);
        // 设置前景色，既二维码颜色（青色）
        config.setForeColor(Color.CYAN.getRGB());
        // 设置背景色（灰色）
        config.setBackColor(Color.GRAY.getRGB());
        //二维码内容
        String qrcode = StringUtils.isEmpty(request.getParameter("qrcode")) ? "http://www.baidu.com" : request.getParameter("qrcode");
        // 生成二维码到文件，也可以到流
        QrCodeUtil.generate(qrcode, config, FileUtil.file("/ruoyi/二维码.jpg"));

        // 默认输出
        //QrCodeUtil.generate(qrcode, 300,300, FileUtil.file("d:/ruoyi/二维码.jpg"));

        return toAjax(1);
    }


    /**
     * 识别二维码
     */
    @Log(title = "识别二维码", businessType = BusinessType.OTHER)
    @GetMapping("/checkQRCode")
    @ResponseBody
    public AjaxResult checkQRCode(HttpServletRequest request) {

        String decode = QrCodeUtil.decode(FileUtil.file("/ruoyi/二维码.jpg"));

        return AjaxResult.success(decode);
    }


    /**
     * redis存入操作
     */
    @Log(title = "redis存入操作", businessType = BusinessType.OTHER)
    @PostMapping("/saveRedis")
    @ResponseBody
    public AjaxResult saveRedis(HttpServletRequest request) {

        String key = request.getParameter("" +
                "key");
        String value = request.getParameter("value");
        if (StringUtils.isAnyBlank(key, value)) {
            return error("请输入key和value!");
        }
        // 默认15s过期；0为不过期
        JedisUtils.set(key, value, 15);
        return success();
    }


    /**
     * redis获取操作
     */
    @Log(title = "redis获取操作", businessType = BusinessType.OTHER)
    @GetMapping("/getRedis/{key}")
    @ResponseBody
    public AjaxResult getRedis(@PathVariable("key") String key) {
        String value = JedisUtils.get(key);
        if (StringUtils.isBlank(value)) {
            return error("该key对应的值不存在或者已过期");
        }
        return success(value);
    }


    /**
     * layui页面
     */
    @GetMapping("/layui")
    public String layui(ModelMap mmap) {
        return prefix + "/layui";
    }

    /**
     * 其他实例页面
     */
    @GetMapping("/other")
    public String other(ModelMap mmap) {
        return prefix + "/other";
    }

    /**
     * 打印demo
     */
    @GetMapping("/print")
    public String print(ModelMap mmap) {
        return prefix + "/print";
    }


    /**
     * 生成PDF
     */
    @Log(title = "生成PDF（demo）", businessType = BusinessType.OTHER)
    @PostMapping("/createPDF")
    @ResponseBody
    public AjaxResult createPDF(HttpServletRequest request) {

        String filePath = "";

        try {
            //给pdf赋值
            Map<String, String> data = new HashMap<String, String>();
            data.put("customername", getLoginName());
            data.put("cardid", "310228198111232012");
            data.put("address", "上海市金山区蒙山路900号");
            data.put("phone", "13627279172");
            data.put("postcode", "200540");
            data.put("contents", "这是一个PDF文件生成测试这是一个PDF文件生成测试这是一个PDF文件生成测试这是一个PDF文件生成测试这是一个PDF文件生成测试这是一个PDF文件生成测试\n这是一个PDF文件生成测试这是一个PDF文件生成测试这是一个PDF文件生成测试@%#￥@&*《》");
            //生成PDF文件
            filePath = PdfUtils.createPDF("/static/docs/pdf导出模版.pdf", "测试pdf导出.pdf", data);

        } catch (Exception ex) {

        }
        return success(filePath);
    }


    /**
     * 生成Word
     *
     * @param request
     * @param response
     * @return
     */
    @Log(title = "生成Word（demo）", businessType = BusinessType.OTHER)
    @PostMapping("/createWord")
    @ResponseBody
    private AjaxResult createWord(HttpServletRequest request, HttpServletResponse response) {


        //需要填充的数据
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("reason", "出差去上海");
        map.put("applyStart", "2018-10-20");
        map.put("applyEnd", "2018-10-21");
        map.put("createDate", "2018-10-19 12:23:45");
        map.put("carType", "SUV");
        map.put("destination", "测试一下");
        map.put("applicant", getLoginName());

        try {

            String path = ResourceUtils.getFile("classpath:static/docs/word导出模版.docx").getPath();
            XWPFDocument doc = WordExportUtil.exportWord07(path, map);
            if (doc != null) {
                org.apache.commons.io.output.ByteArrayOutputStream bos = new org.apache.commons.io.output.ByteArrayOutputStream();
                doc.write(bos);
                byte[] content = bos.toByteArray();
                //设置word的名字
                String fileName = "word导出实例";
                //乱码设置
                if (isIE(request)) {
                    fileName = java.net.URLEncoder.encode(fileName, "UTF8");
                } else {
                    fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
                }
//                response.setHeader("content-disposition", "attachment;filename=" + fileName + ".docx");
//                ServletOutputStream out = response.getOutputStream();
//                out.write(content);
//                out.flush();
//                out.close();

                response.reset();
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".docx");
                response.addHeader("Content-Length", "" + content.length);
                //response.setContentType("application/msword; charset=UTF-8");

//                response.setContentType( "application/msword" );
//                response.setContentType("application/vnd.ms-excel");
//                response.setHeader("Content-type","application/pdf");

                IOUtils.write(content, response.getOutputStream());
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
        return AjaxResult.success("成功");
    }


    /**
     * 生成Excel
     *
     * @param request
     * @param response
     * @return
     */
    @Log(title = "生成Excel（demo）", businessType = BusinessType.OTHER)
    @PostMapping("/createExcel")
    private String createExcel(HttpServletRequest request, HttpServletResponse response) {


        String path = null;
        try {
            path = ResourceUtils.getFile("classpath:static/docs/excel导出模版.xls").getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        TemplateExportParams params = new TemplateExportParams(path);
        params.setSheetName("excel导出实例");
        Map<String, Object> map = new HashMap<>();
        map.put("unitAbbreviation", "测试");
        map.put("trainName", "科目1");
        map.put("courseName", "参数1");
        map.put("instructorName", "参数2");
        map.put("instructorProfessionalTitle", "参数3");
        map.put("instructorBankNo", "参数4");
        map.put("instructorDepositBank", "参数5");
        map.put("coursePeroid", "参数36");

        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        if (workbook != null) {
            org.apache.commons.io.output.ByteArrayOutputStream bos = new org.apache.commons.io.output.ByteArrayOutputStream();
            try {
                workbook.write(bos);
                byte[] content = bos.toByteArray();
                //设置excel的名字
                String fileName = "excel导出实例";
                //乱码设置
                if (isIE(request)) {
                    fileName = java.net.URLEncoder.encode(fileName, "UTF8");
                } else {
                    fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
                }
                response.setHeader("content-disposition", "attachment;filename=" + fileName + ".xls");
                ServletOutputStream out = response.getOutputStream();
                out.write(content);
                out.flush();
                out.close();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

}
