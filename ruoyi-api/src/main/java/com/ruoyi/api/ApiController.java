package com.ruoyi.api;

import cn.hutool.json.JSONUtil;
import com.ruoyi.area.auth.domain.AuthAccessToken;
import com.ruoyi.area.auth.service.IAuthAccessTokenService;
import com.ruoyi.base.ApiBaseController;
import com.ruoyi.common.annotation.ValidateAccessToken;
import com.ruoyi.common.enums.ResponseCode;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 通过Access Token访问的API服务
 *
 * @author tao.liang
 * @date 2019/7/24
 */
@Api(value = "/", description = "API接口", tags = "API接口")
@RestController
public class ApiController extends ApiBaseController {
    @Autowired
    private IAuthAccessTokenService authAccessTokenService;

    @Autowired
    private ISysUserService userService;

    @ApiOperation("获取个人信息")
    @ValidateAccessToken
    @PostMapping(value = "/me")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access_token", value = "code", required = true, paramType = "query")
    })
    public String getUserInfo(@RequestParam(name="access_token") String accessToken){
        //查询数据库中的Access Token
        AuthAccessToken authAccessToken = authAccessTokenService.selectByAccessToken(accessToken);

        if(authAccessToken != null){
            SysUser user = userService.selectUserById(authAccessToken.getUserId());
            return JSONUtil.toJsonStr(user);
        }else{
            return JSONUtil.toJsonStr(generateErrorResponse(ResponseCode.INVALID_GRANT));
        }
    }

}
