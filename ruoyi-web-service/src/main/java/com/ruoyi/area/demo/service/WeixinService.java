package com.ruoyi.area.demo.service;//package com.ruoyi.area.demo.service;
//
//import com.ruoyi.config.WxMpConfig;
//import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
//import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//
///**
// * 微信Service
// *
// * @author Tao.Liang
// *
// */
//@Service
//public class WeixinService extends WxMpServiceImpl {
//
//  @Autowired
//  private WxMpConfig wxConfig;
//
//  @PostConstruct
//  public void init() {
//    final WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
//    // 设置微信公众号的appid
//    config.setAppId(this.wxConfig.getAppid());
//    // 设置微信公众号的app corpSecret
//    config.setSecret(this.wxConfig.getAppsecret());
//    // 设置微信公众号的token
//    config.setToken(this.wxConfig.getToken());
//    // 设置消息加解密密钥
//    config.setAesKey(this.wxConfig.getAesKey());
//    super.setWxMpConfigStorage(config);
//  }
//}
