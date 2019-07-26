package com.ruoyi.common.enums;

/**
 * @Description restful接口返回code
 * @Author yufei
 * @Date 2019-03-05 21:56
 **/
public enum ResponseCode {

    FAILED(-1L, "操作失败！请联系管理员！"),
    SUCCESS(200L, "成功"),
    ERROR_REQUEST(4001, "请求参数错误"),
    OVER_TIME(4002, "请求参数过期"),
    KEY_NOT_FOUNT(4003, "AppKey不存在"),
    RE_REQUEST(4004, "请求过于频繁"),
    ILLEGAL_REQUEST(4005, "非法请求"),
    ILLEGAL_ACCOUNT(4006, "账号信息不存在"),
    ERROR_LOGIN(4007, "账号或密码错误"),
    ILLEGAL_TOKEN(4008, "非法请求（token验证失败）"),
    EXPIRE_TOKEN(4009, "非法请求（token失效）"),
    INVALID_REQUEST(4010, "请求缺少某个必需参数，包含一个不支持的参数或参数值，或者格式不正确。"),
    INVALID_CLIENT(4011, "请求的client_id或client_secret参数无效。"),
    INVALID_GRANT(4012, "请求的Authorization Code、Access Token、Refresh Token等信息是无效的。"),
    UNSUPPORTED_GRANT_TYPE(4013, "不支持的grant_type。"),
    INVALID_USERNAME_PASSWORD(4014, "用户账号或密码错误。"),
    EXPIRED_TOKEN(4015, "请求的Access Token或Refresh Token已过期。"),
    REDIRECT_URI_MISMATCH(4016, "请求的redirect_uri所在的域名与开发者注册应用时所填写的域名不匹配。"),
    INVALID_REDIRECT_URI(4017, "请求的回调URL不在白名单中。"),
    UNKNOWN_ERROR(4018, "程序发生未知异常，请联系管理员解决。");

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
