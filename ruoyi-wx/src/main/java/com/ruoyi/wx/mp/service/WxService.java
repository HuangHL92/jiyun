package com.ruoyi.wx.mp.service;

import com.ruoyi.wx.mp.config.WxConfigStorage;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;

public class WxService extends WxMpServiceImpl {

    protected WxConfigStorage wxConfigStorage;

    public WxConfigStorage getWxConfigStorage() {
        return wxConfigStorage;
    }

    public void setWxConfigStorage(WxConfigStorage wxConfigStorage) {
        this.wxConfigStorage = wxConfigStorage;
        this.initHttp();
    }
    @Override
    public WxMpConfigStorage getWxMpConfigStorage() {
        return this.wxConfigStorage;
    }

}
