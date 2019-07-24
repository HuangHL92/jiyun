package com.ruoyi.web.controller.wap;

import com.ruoyi.framework.web.base.BaseController;
import com.ruoyi.wx.mp.config.WxMpConfiguration;
import com.ruoyi.wx.mp.service.WxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLDecoder;
import java.net.URLEncoder;


/**
 * 移动端
 *
 * @author tao.liang
 * @date 2019-02-26
 */
@Controller
@RequestMapping("/wap/wechat")
public class WechatController extends BaseController {


    private String prefix = "wap/wechat";

    /**
     * 网页授权Demo
     * @param appid
     * @param returnUrl
     * @return
     */
    @GetMapping(value = "access")
    public String  access(@RequestParam(value = "appid") String appid, @RequestParam(value = "returnUrl",required = false) String returnUrl) {
        WxService wxService = WxMpConfiguration.getMpServices().get(appid);
        String webUrl = wxService.getWxConfigStorage().getWebPath();

        return "redirect:" + webUrl + "/wx/auth?appid=" + appid + "&returnUrl=" + URLEncoder.encode(returnUrl==null?"":returnUrl);
    }

    /**
     * 首页(关注完成跳转首页)
     * @return
     */
    @RequestMapping(value = "index")
    public String index(@RequestParam("openId") String openId,
                       @RequestParam("nickName") String nickName,
                       @RequestParam("headImgUrl") String headImgUrl,
                       Model model) {
        model.addAttribute("openId", openId);
        model.addAttribute("nickName", URLDecoder.decode(nickName));
        model.addAttribute("headImgUrl", headImgUrl);

        return prefix + "/index";
    }



}
