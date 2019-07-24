package com.ruoyi.wx.mp.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.ruoyi.wx.mp.config.WxMpConfiguration;
import com.ruoyi.wx.mp.config.WxMpProperties;
import com.ruoyi.wx.mp.service.WxService;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@Controller
@RequestMapping("/wx")
public class WxAuthController {


    //随机生成密钥
    byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();

    //构建
    SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);

    @RequestMapping("auth")
    public String auth(@RequestParam(value = "appid") String appid, @RequestParam(value = "returnUrl",required = false) String returnUrl) throws IOException {

        WxService wxService = WxMpConfiguration.getMpServices().get(appid);
        String webUrl = wxService.getWxConfigStorage().getWebPath();
        String thirdUrl = wxService.getWxConfigStorage().getThirdUrl();

        if(StringUtils.isNotEmpty(returnUrl)) {
            if(!returnUrl.startsWith("http:")) {
                returnUrl = URLEncoder.encode(webUrl + returnUrl);
            }
        } else {
            returnUrl="/wap/wechat/index";
        }

        String rUrl =""; //跳转地址
        String redirectUri = webUrl + "/wx/userInfo"; //微信回调地址
        String state = appid + "#" + returnUrl;


        //加密
        byte[] encrypt = aes.encrypt(state);
        //解密
        byte[] decrypt = aes.decrypt(encrypt);

        //加密为16进制表示
         state = aes.encryptHex(state);


        if(StringUtils.isEmpty(thirdUrl)) {
            rUrl = wxService.oauth2buildAuthorizationUrl(redirectUri,
                    WxConsts.OAuth2Scope.SNSAPI_USERINFO, returnUrl);
        } else {
            rUrl = thirdUrl+ "?appid=" + appid + "&scope=" + WxConsts.OAuth2Scope.SNSAPI_USERINFO + "&state=" +state+ "&redirect_uri=" +redirectUri;
        }
        return "redirect:" + rUrl;
    }


    /**
     * 换取微信用户openId&&获得用户信息
     * @param code
     * @param state
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code, @RequestParam(value = "state",required = false) String state) throws WxErrorException {

        //解密为字符串
        state= aes.decryptStr(state);

        String appid = "";
        String returnUrl="";
        if(StringUtils.isNotEmpty(state)) {
            appid = state.split("#")[0];
            returnUrl = state.split("#")[1];
        }
        WxMpService wxService = WxMpConfiguration.getMpServices().get(appid);
        // 获得access token
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxService.oauth2getAccessToken(code);
        // 获得用户基本信息
        WxMpUser wxMpUser = wxService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);

        //WxMpUser 对的=wxService.getUserService().userInfo(wxMpUser.getOpenId());

        //获取openId
        String openId = wxMpUser.getOpenId();
        String nickName = URLEncoder.encode(wxMpUser.getNickname());
        String headImgUrl = wxMpUser.getHeadImgUrl();
        return "redirect:" + returnUrl+ "?openId=" + openId + "&nickName=" + nickName + "&headImgUrl=" + headImgUrl;
    }

}
