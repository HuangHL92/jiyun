package com.ruoyi.api;

import cn.hutool.json.JSONUtil;
import com.ruoyi.area.auth.domain.AuthAccessToken;
import com.ruoyi.area.auth.service.IAuthAccessTokenService;
import com.ruoyi.base.ApiBaseController;
import com.ruoyi.common.enums.ResponseCode;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 通过Access Token访问的API服务
 *
 * @author tao.liang
 * @date 2019/7/24
 */
@RestController
@RequestMapping("/api")
public class ApiController extends ApiBaseController {
    @Autowired
    private IAuthAccessTokenService authAccessTokenService;

    @Autowired
    private ISysUserService userService;

    @PostMapping(value = "/users/getInfo", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getUserInfo(HttpServletRequest request){
        String accessToken = request.getParameter("access_token");
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
