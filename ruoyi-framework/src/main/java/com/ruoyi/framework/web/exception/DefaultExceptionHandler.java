package com.ruoyi.framework.web.exception;

import cn.hutool.extra.mail.MailUtil;
import com.ruoyi.common.exception.OAuth2Exception;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.exception.DemoModeException;
import com.ruoyi.framework.util.PermissionUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 自定义异常处理器
 * 
 * @author ruoyi
 */
@RestControllerAdvice
public class DefaultExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @Value("${jiyun.notice.mailto}")
    private String mailto;

    @Value("${jiyun.notice.isopen}")
    private boolean isopen;

    @Value("${ruoyi.name}")
    private String systemName;

    /**
     * 权限校验失败
     */
    @ExceptionHandler(AuthorizationException.class)
    public AjaxResult handleAuthorizationException(AuthorizationException e)
    {
        doError(e);
        return AjaxResult.error(PermissionUtils.getMsg(e.getMessage()));
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
    public AjaxResult handleException(HttpRequestMethodNotSupportedException e)
    {
        doError(e);
        return AjaxResult.error("不支持' " + e.getMethod() + "'请求");
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public AjaxResult notFount(RuntimeException e)
    {
        doError(e);
        return AjaxResult.error("运行时异常:" + e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e)
    {
        doError(e);
        return AjaxResult.error("服务器错误，请联系管理员");
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public AjaxResult businessException(BusinessException e)
    {

        doError(e);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * OAuth2异常
     */
    @ExceptionHandler(OAuth2Exception.class)
    public AjaxResult oAuth2ExceptionException(OAuth2Exception e)
    {

        doError(e);
        return AjaxResult.error(e.getCode(), e.getMessage());
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public AjaxResult demoModeException(DemoModeException e)
    {
        doError(e);
        return AjaxResult.error("演示模式，不允许操作");
    }


    /**
     * 写日志、发送邮件通知
     * @param e
     */
    private void doError(Exception e) {

        log.error(e.getMessage(), e);
        if(isopen) {
            MailUtil.send(mailto,"[" + systemName+"]异常通知",getExceptionDetail(e),true);
        }
    }

    private static String getExceptionDetail(Throwable e){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }

}
