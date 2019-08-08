package com.ruoyi.base;

import com.ruoyi.common.base.ApiResult;
import com.ruoyi.common.enums.ResponseCode;
import com.ruoyi.common.exception.ApiRuntimeException;
import com.ruoyi.common.exception.user.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description $功能描述$
 * @Author yufei
 * @Date 2019-03-06 09:20
 **/
public class ApiBaseController {

    /**
     * 日志对象
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public ApiBaseController() {
    }

    protected <T> ApiResult<T> success() {
        return ApiResult.success(null);
    }

    protected <T> ApiResult<T> success(T data) {
        return ApiResult.success(data);
    }

    protected <T> ApiResult<T> success(String msg, T data) {
        return ApiResult.success(msg,data);
    }

    protected <T> ApiResult<T> error(String msg) {
        return ApiResult.error(msg);
    }

    protected <T> ApiResult<T> error(ResponseCode errorCode) {
        return ApiResult.error(errorCode);
    }

    /**
     * <p>
     * 自定义 REST 业务异常
     * <p>
     *
     * @param e 异常类型
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ApiResult<Object> handleBadRequest(Exception e) {

        /*
         * 业务逻辑异常
         */
        if (e instanceof ApiRuntimeException) {
            ResponseCode errorCode = ((ApiRuntimeException) e).getErrorCode();
            if (null != errorCode) {
                logger.debug("Rest request error, {}", errorCode.toString());
                return ApiResult.error(errorCode);
            }
            logger.debug("Rest request error, {}", e.getMessage());
            return ApiResult.error(e.getMessage());
        }

        /*
         * 用户信息异常
         */
        if (e instanceof UserException) {
            logger.debug("Rest request error, {}", e.getMessage());
            return ApiResult.error(e.getMessage());
        }

        /*
         * 参数校验异常
         */
        if (e instanceof BindException) {
            BindingResult bindingResult = ((BindException) e).getBindingResult();
            if (null != bindingResult && bindingResult.hasErrors()) {
                List<Object> jsonList = new ArrayList<>();
                bindingResult.getFieldErrors().stream().forEach(fieldError -> {
                    Map<String, Object> jsonObject = new HashMap<>(2);
                    jsonObject.put("name", fieldError.getField());
                    jsonObject.put("msg", fieldError.getDefaultMessage());
                    jsonList.add(jsonObject);
                });
                return ApiResult.restResult(jsonList, ResponseCode.FAILED);
            }
        }

        /**
         * 系统内部异常，打印异常栈
         */
        logger.error("Error: handleBadRequest StackTrace : {}", e);

        return ApiResult.error(ResponseCode.FAILED,e.getMessage());
    }

    /**
     * 组装错误请求的返回
     */
    protected void generateErrorResponse(Map<String,Object> result, ResponseCode responseCode) {
        result.put("code", responseCode.getCode());
        result.put("msg",responseCode.getMsg());
    }

    /**
     * 组装错误请求的返回
     */
    protected Map<String,Object> generateErrorResponse(ResponseCode responseCode) {
        Map<String,Object> result = new HashMap<>(2);
        result.put("code", responseCode.getCode());
        result.put("msg",responseCode.getMsg());
        return result;
    }


}
