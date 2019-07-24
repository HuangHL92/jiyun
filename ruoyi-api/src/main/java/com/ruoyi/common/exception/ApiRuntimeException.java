package com.ruoyi.common.exception;

import com.ruoyi.common.enums.ResponseCode;

/**
 * @Description restful接口异常
 * @Author yufei
 * @Date 2019-03-05 21:58
 **/
public class ApiRuntimeException extends RuntimeException{

    private ResponseCode errorCode;

    public ApiRuntimeException(ResponseCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }

    public ApiRuntimeException(String message) {
        super(message);
    }

    public ApiRuntimeException(Throwable cause) {
        super(cause);
    }

    public ApiRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseCode getErrorCode() {
        return this.errorCode;
    }

}
