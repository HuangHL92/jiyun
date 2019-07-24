package com.ruoyi.framework.web.service;

import org.springframework.stereotype.Service;

/**
 * 页面通用方法
 * 
 * @author ruoyi
 */
@Service("comm")
public class CommService
{


    /***
     * 图片地址处理
     * @param url
     * @return
     */
    public String getImgUrl( String url) {
        if(url.startsWith("http")) {
            return url;
        } else {
            return "/profile/upload/" + url;
        }

    }
}
