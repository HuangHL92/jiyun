package com.ruoyi.api;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.base.ApiBaseController;
import com.ruoyi.common.annotation.ValidateRequest;
import com.ruoyi.common.base.ApiResult;
import com.ruoyi.common.enums.ResponseCode;
import com.ruoyi.common.utils.JedisUtils;
import com.ruoyi.common.utils.RegexUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.workday.WorkdayUtils;
import com.ruoyi.system.domain.SysCalendar;
import com.ruoyi.system.service.ISysCalendarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Description $功能描述$
 * @Author yufei
 * @Date 2019-03-17 21:21
 **/
@Api(value = "/comm", description = "基础服务接口")
@RestController
@RequestMapping("/api/comm/*")
public class CommController extends ApiBaseController {

    @Value("${jiyun.shortapi.url}")
    private String sinaShortApiUrl;

    @Autowired
    private ISysCalendarService calendarService;

    private String VCODE_KEY= "vcodeCache:%s";

    @ApiOperation("手机获得验证码（此时临时模拟直接返回，实际通过短信发送）")
    @PostMapping("vcodeSms")
    @ValidateRequest
    public ApiResult vcodeSms(@RequestParam(name="mobile") String mobile)
    {

        //1.参数验证
        if(StringUtils.isEmpty(mobile)) {
            return ApiResult.error(ResponseCode.ILLEGAL_REQUEST);
        }

        //2.证验是否未有为有效的手机号
        if(!RegexUtils.isMobile(mobile)){
            return ApiResult.error("不是有效的手机号码！");
        }

        //3. 验证码信息写入redis（验证码有效期5分钟）
        String vcode = RandomUtil.randomNumbers(6);
        JedisUtils.set(String.format(VCODE_KEY,mobile), vcode,300);

        //4.发送短信至手机 TODO 此时临时模拟直接返回，实际通过短信发送
        HashMap map =new HashMap();
        map.put("code",vcode);

        return ApiResult.success(map);
    }


    @ApiOperation("邮箱获得验证码")
    @PostMapping("vcodeMail")
    @ValidateRequest
    public ApiResult vcodeMail(@RequestParam(name="mail") String mail)
    {

        //1.参数验证
        if(StringUtils.isEmpty(mail)) {
            return ApiResult.error(ResponseCode.ILLEGAL_REQUEST);
        }

        //2.证验是否未有为有效的手机号
        if(!RegexUtils.isEmail(mail)){
            return ApiResult.error("不是有效的邮箱地址！");
        }

        //3. 验证码信息写入redis（验证码有效期5分钟）
        String vcode = RandomUtil.randomNumbers(6);
        JedisUtils.set(String.format(VCODE_KEY,mail), vcode,300);

        //4.发送短信至手机 TODO 此时临时模拟直接返回，实际通过短信发送
        MailUtil.send(mail,"吉运Java开发平台验证码","您的验证码为：" + vcode + "（5分钟内有效）",true);
        return ApiResult.success();
    }


    @ApiOperation("验证校验(手机)")
    @PostMapping("vcodeCheck")
    @ValidateRequest
    public ApiResult vcodeCheck(@RequestParam(name="mobile") String mobile, @RequestParam(name="code") String code)
    {

        //1.参数验证
        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(code)) {
            return ApiResult.error(ResponseCode.ILLEGAL_REQUEST);
        }

        //2.证验是否未有为有效的手机号
        if(!RegexUtils.isMobile(mobile)){
            return ApiResult.error("不是有效的手机号码！");
        }

        //3. 从redis取的原始验证码
        String ocode = JedisUtils.get(String.format(VCODE_KEY,mobile));
        if(StringUtils.isEmpty(ocode)) {
            return ApiResult.error("验证码不存在或已失效！");
        }
        if(!ocode.equals(code)){
            return ApiResult.error("验证码错误！");
        }
        return ApiResult.success();
    }


    @ApiOperation("工作日计算（指定日期之后N个工作日是几号）")
    @GetMapping("calander4workday")
    public ApiResult calander4workday(@RequestParam(name="cdate") String cdate, @RequestParam(name="days") int days)
    {

        //1.参数验证
        if(StringUtils.isEmpty(cdate)) {
            return ApiResult.error(ResponseCode.ILLEGAL_REQUEST);
        }

        try {
            HashMap<Integer,Integer> map = new HashMap<>();
            QueryWrapper<SysCalendar> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("years",DateUtil.parse(cdate).year());
            List<SysCalendar> list = calendarService.list(queryWrapper);
            for (SysCalendar obj : list){
                map.put(obj.getDays(),obj.getDayType());
            }
            WorkdayUtils.init(map);

            Date date = WorkdayUtils.getIncomeDate(DateUtil.parse(cdate),days);
            return ApiResult.success(date);
        }catch (Exception ex) {
            return ApiResult.error("日期格式错误！");
        }



    }


    @ApiOperation("工作日计算（计算两个日期之间有几个工作日）")
    @GetMapping("calander4days")
    public ApiResult calander4days(@RequestParam(name="sdate") String sdate, @RequestParam(name="edate") String edate)
    {

        //1.参数验证
        if(StringUtils.isEmpty(sdate) || StringUtils.isEmpty(edate)) {
            return ApiResult.error(ResponseCode.ILLEGAL_REQUEST);
        }

        int days = 0;
        try {
            //日历初始化（休息日）
            HashMap<Integer,Integer> map = new HashMap<>();
            QueryWrapper<SysCalendar> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("days",Integer.parseInt(sdate.replaceAll("-","")),Integer.parseInt(edate.replaceAll("-","")));
            List<SysCalendar> list = calendarService.list(queryWrapper);
            for (SysCalendar obj : list){
                map.put(obj.getDays(),obj.getDayType());
            }
            WorkdayUtils.init(map);

            days = WorkdayUtils.howManyWorkday(DateUtil.parse(sdate),DateUtil.parse(edate));

        }catch (Exception ex) {
            return ApiResult.error("日期格式错误！日期格式为：YYYY-MM-dd");
        }

        return ApiResult.success(days);
    }

    /**
     * 短网址计算
     * @param url
     * @return short_url
     */
    @ApiOperation("短网址计算（计算目标url的短网址）")
    @GetMapping("getShortUrl")
    public ApiResult getShortUrl(@RequestParam(name="url") String url)
    {
        //1.参数验证
        if(StringUtils.isEmpty(url)) {
            return ApiResult.error(ResponseCode.ERROR_REQUEST);
        }
        //验证url是否有效
        if (!RegexUtils.isUrl(url)){
            return ApiResult.error("无效的url!");
        }

        try {
            //拼接url拿到url_short字段
            String result = HttpUtil.get(sinaShortApiUrl + url);
            JSONObject jsonObject = JSONUtil.parseArray(result).getJSONObject(0);
            String shortUrl = (String) jsonObject.get("url_short");

            if (StringUtils.isEmpty(shortUrl)){
                return ApiResult.error("无效的url!");
            }

            return ApiResult.success(shortUrl);
        }catch (Exception ex) {
            return ApiResult.error("无效的url!");
        }
    }

}
