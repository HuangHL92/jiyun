package com.ruoyi.common;

/**
 * 公共常量类
 *
 * @author tao.liang
 * @date 2019/7/24
 */
public class AuthConstants {
    /**
     * 用户信息在session中存储的变量名
     */
    public static final String SESSION_USER = "SESSION_USER";

    /**
     * 登录页面的回调地址在session中存储的变量名
     */
    public static final String SESSION_LOGIN_REDIRECT_URL = "LOGIN_REDIRECT_URL";

    /**
     * 授权页面的回调地址在session中存储的变量名
     */
    public static final String SESSION_AUTH_REDIRECT_URL = "SESSION_AUTH_REDIRECT_URL";

    /**
     * 记住我cookie的MD5加密密钥
     */
    public static final String AEC_KEY = "fCq+/xW818hJSJY+cmJ3aQ==";

    /**
     * 二维码内容头
     */
    public static final String QRCODE_HEADER = "BM_ANGEL:qrcode_scan_login#";

    /**
     * 二维码内容key
     */
    public static final String QRCODE_LOGIN = "qrcode_login_";
}
