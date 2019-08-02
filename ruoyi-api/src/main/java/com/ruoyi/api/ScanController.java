package com.ruoyi.api;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ruoyi.area.auth.domain.AuthAccessToken;
import com.ruoyi.area.auth.service.IAuthAccessTokenService;
import com.ruoyi.base.ApiBaseController;
import com.ruoyi.common.AuthConstants;
import com.ruoyi.common.annotation.ValidateAccessToken;
import com.ruoyi.common.base.ApiResult;
import com.ruoyi.common.enums.QrCodeEnmu;
import com.ruoyi.common.enums.ResponseCode;
import com.ruoyi.framework.redis.RedisService;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.hibernate.validator.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.concurrent.TimeUnit;

/**
 * 扫码控制，APP Server端处理
 *
 * @author tao.liang
 * @date 2019/8/02
 */
@ApiIgnore()
@RestController
@RequestMapping("/scan")
public class ScanController extends ApiBaseController {

    @Autowired
    private RedisService redisService;
    @Autowired
    private IAuthAccessTokenService authAccessTokenService;
    @Autowired
    private ISysUserService userService;

    /**
     * App扫一扫统一入口
     *
     * @param message
     * @param accessToken
     * @return
     */
    @PostMapping("/scanService")
    @ValidateAccessToken
    public ApiResult scanService(@RequestParam("message") String message,
                                 @RequestParam("access_token") String accessToken) {
        //APP扫描到的内容信息
        if (message.startsWith(AuthConstants.QRCODE_HEADER)) {//扫码登录
            //获取uuid，校验redis中是否存在该标识
            String uuid = message.replace(AuthConstants.QRCODE_HEADER, "");
            String key = AuthConstants.QRCODE_LOGIN + uuid;
            if (!redisService.exists(key)) {
                //如果不存在该KEY，表示二维码已经失效
                return error("二维码已经失效，获取信息失败，请重新获取");
            } else {
                //更新二维码，并将二维码唯一标识与token绑定，有效时间120S
                redisService.setWithExpire(key, QrCodeEnmu.scan.toString(), 120, TimeUnit.SECONDS);
                redisService.setWithExpire("qrcode_" + accessToken, uuid, 120, TimeUnit.SECONDS);
                return success();
            }
        } else {
            //其他类型的请求操作
        }
        return success();
    }

    /**
     * 扫码登录：确认/取消登录
     * @param type
     * @param accessToken
     * @return
     */
    @PostMapping("/scanLogin")
    @ValidateAccessToken
    public ApiResult scanLogin(@RequestParam("type") String type,
                               @RequestParam("access_token") String accessToken) {
        // 参数验证
        if (StrUtil.isEmpty(type) || StrUtil.isEmpty(accessToken)) {
            return error(ResponseCode.ERROR_REQUEST);
        }

        //根据token获取绑定的uuid，并校验是否已失效
        if (!redisService.exists("qrcode_" + accessToken)) {
            return success(ResponseCode.GET_MESSAGE_FAILED);
        } else {
            String uuid = redisService.getObj("qrcode_" + accessToken);
            if (QrCodeEnmu.login.toString().equals(type)) {
                //根据token获取用户信息
                //查询数据库中的Access Token
                AuthAccessToken authAccessToken = authAccessTokenService.selectByAccessToken(accessToken);
                String username;
                if(authAccessToken != null){
                    SysUser user = userService.selectUserById(authAccessToken.getUserId());
                    username = user.getLoginName();
                }else{
                    return error(ResponseCode.INVALID_GRANT);
                }

                //更新二维码状态，并附上用户信息
                redisService.setWithExpire(AuthConstants.QRCODE_LOGIN + uuid, "login_" + username, 120, TimeUnit.SECONDS);
                //删除绑定了的token与uuid
                redisService.del("qrcode_" + accessToken);
                return success();
            } else if (QrCodeEnmu.cancel.toString().equals(type)) {
                redisService.setWithExpire(AuthConstants.QRCODE_LOGIN + uuid, "cancel", 120, TimeUnit.SECONDS);
                //删除绑定了的token与uuid
                redisService.del("qrcode_" + accessToken);
                return success(ResponseCode.CANCEL_SUCCESS);
            }
        }
        return success();
    }

}
