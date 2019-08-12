package com.ruoyi.common.enums;

/**
 * @Description restful接口返回code
 * @Author yufei
 * @Date 2019-03-05 21:56
 **/
public enum ResponseCode {

    FAILED(-1L, "操作失败！请联系管理员！"),
    SUCCESS(200L, "成功"),
    SERVER_ERROR(4000, "服务器繁忙，请稍后再试"),
    UNKNOWN_ERROR(4001, "程序发生未知异常，请联系管理员解决。"),
    ERROR_REQUEST(4002, "请求参数错误"),
    ILLEGAL_REQUEST(4003, "非法请求"),
    ILLEGAL_ACCOUNT(4004, "账号信息不存在"),
    ERROR_LOGIN(4005, "账号或密码错误"),
    PASSWORD_RETRY_LIMIT_EXCEED(4006, "密码输入错误次数已达限制，帐户锁定10分钟"),
    INVALID_REQUEST(4100, "请求缺少某个必需参数，包含一个不支持的参数或参数值，或者格式不正确。"),
    INVALID_CLIENT(4101, "请求的client_id或client_secret参数无效。"),
    INVALID_GRANT(4102, "请求的Authorization Code、Access Token、Refresh Token等信息是无效的。"),
    UNSUPPORTED_GRANT_TYPE(4103, "不支持的grant_type。"),
    INVALID_USERNAME_PASSWORD(4104, "用户账号或密码错误。"),
    EXPIRED_TOKEN(4105, "请求的Access Token或Refresh Token已过期。"),
    REDIRECT_URI_MISMATCH(4106, "请求的redirect_uri所在的域名与开发者注册应用时所填写的域名不匹配。"),
    INVALID_REDIRECT_URI(4107, "请求的回调URL不在白名单中。"),
    INVALID_AUTH_SCOPE(4108, "没有该权限范围。"),
    INVALID_QRCODE(4200, "二维码已失效，请重新获取"),
    CANCEL_SUCCESS(4201, "取消成功"),
    GET_MESSAGE_FAILED(4202, "获取信息失败，请重新获取"),
    REQUEST_TIME_OUT(4203, "请求超时，请重新请求"),
    SCAN_SUCCESS(4204, "扫描成功");

    private long code;
    private String msg;

    ResponseCode(final long code, final String msg) {
        this.code = code;
        this.msg = msg;
    }

    public long getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


}
