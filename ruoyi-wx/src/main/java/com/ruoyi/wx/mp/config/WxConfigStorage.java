package com.ruoyi.wx.mp.config;

import lombok.Data;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;

@Data
public class WxConfigStorage extends WxMpInMemoryConfigStorage {
    protected volatile String webPath;
    protected volatile String thirdUrl;


}
