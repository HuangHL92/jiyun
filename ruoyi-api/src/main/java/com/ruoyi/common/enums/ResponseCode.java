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
    EXPIRE_TOKEN(4009, "非法请求（token失效）");

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
