package com.ruoyi.api;

import com.ruoyi.base.ApiBaseController;
import com.ruoyi.common.base.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "/sms", description = "短信接口服务")
@RestController
@RequestMapping("/api/sms/*")
public class SMSController extends ApiBaseController {


    @ApiOperation("发送短信（单条）")
    @PostMapping("send")
    public ApiResult send(String mobile,String content, String account,String password)
    {
        //1.参数验证
        if(StringUtils.isEmpty(account) || StringUtils.isEmpty(password)
                || StringUtils.isEmpty(mobile) || StringUtils.isEmpty(content) ) {
            return ApiResult.error("参数错误");
        }
        //TODO 2.验证账号密码

        //TODO 3.发送短信

        return ApiResult.success();
    }
}
