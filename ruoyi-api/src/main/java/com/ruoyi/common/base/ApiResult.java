package com.ruoyi.common.base;

import com.ruoyi.common.enums.ResponseCode;
import com.ruoyi.common.exception.ApiRuntimeException;

import java.io.Serializable;

/**
 * @Description restful返回值
 * @Author yufei
 * @Date 2019-03-05 21:55
 **/
public class ApiResult<T> implements Serializable {

    private long code;
    private T data;
    private String msg;

    public ApiResult() {
    }

    public ApiResult(ResponseCode errorCode) {
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }


    public static <T> ApiResult<T> success() {
        ResponseCode aec = ResponseCode.SUCCESS;

        return restResult(null, aec);
    }


    public static <T> ApiResult<T> success(T data) {
        ResponseCode aec = ResponseCode.SUCCESS;
        if (data instanceof Boolean && Boolean.FALSE.equals(data)) {
            aec = ResponseCode.FAILED;
        }

        return restResult(data, aec);
    }


    public static <T> ApiResult<T> success(String msg, T data) {
        ResponseCode aec = ResponseCode.SUCCESS;
        if (data instanceof Boolean && Boolean.FALSE.equals(data)) {
            aec = ResponseCode.FAILED;
        }

        return restResult(data, aec, msg);
    }

    public static <T> ApiResult<T> error(String msg) {
        return restResult(null, ResponseCode.FAILED.getCode(), msg);
    }

    public static <T> ApiResult<T> error(ResponseCode errorCode) {
        return restResult(null, errorCode);
    }

    public static <T> ApiResult<T> error(ResponseCode errorCode, String msg) {
        return restResult(null, errorCode.getCode(), msg);
    }


    public static <T> ApiResult<T> restResult(T data, ResponseCode errorCode) {
        return restResult(data, errorCode.getCode(), errorCode.getMsg());
    }

    public static <T> ApiResult<T> restResult(T data, ResponseCode errorCode, String msg) {
        return restResult(data, errorCode.getCode(), msg);
    }

    private static <T> ApiResult<T> restResult(T data, long code, String msg) {
        ApiResult<T> apiResult = new ApiResult();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    public boolean ok() {
        return ResponseCode.SUCCESS.getCode() == this.code;
    }

    public T serviceData() {
        if (!this.ok()) {
            throw new ApiRuntimeException(this.msg);
        } else {
            return this.data;
        }
    }

    public long getCode() {
        return this.code;
    }

    public T getData() {
        return this.data;
    }

    public String getMsg() {
        return this.msg;
    }

    public ApiResult<T> setCode(long code) {
        this.code = code;
        return this;
    }

    public ApiResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    public ApiResult<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ApiResult)) {
            return false;
        } else {
            ApiResult<?> other = (ApiResult)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getCode() != other.getCode()) {
                return false;
            } else {
                Object this$data = this.getData();
                Object other$data = other.getData();
                if (this$data == null) {
                    if (other$data != null) {
                        return false;
                    }
                } else if (!this$data.equals(other$data)) {
                    return false;
                }

                Object this$msg = this.getMsg();
                Object other$msg = other.getMsg();
                if (this$msg == null) {
                    if (other$msg != null) {
                        return false;
                    }
                } else if (!this$msg.equals(other$msg)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ApiResult;
    }

    @Override
    public int hashCode() {
        int result = 1;
        long $code = this.getCode();
        result = result * 59 + (int)($code >>> 32 ^ $code);
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        Object $msg = this.getMsg();
        result = result * 59 + ($msg == null ? 43 : $msg.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "R(code=" + this.getCode() + ", data=" + this.getData() + ", msg=" + this.getMsg() + ")";
    }

}
