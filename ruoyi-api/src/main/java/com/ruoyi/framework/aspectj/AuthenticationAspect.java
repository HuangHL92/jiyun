package com.ruoyi.framework.aspectj;

import com.ruoyi.area.auth.domain.AuthAccessToken;
import com.ruoyi.area.auth.service.IAuthAccessTokenService;
import com.ruoyi.common.annotation.ValidateAccessToken;
import com.ruoyi.common.enums.ResponseCode;
import com.ruoyi.common.exception.ApiRuntimeException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.JedisUtils;
import com.ruoyi.common.utils.ServletUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @Description RestfulApi 接口验证切面
 * @Author yufei
 * @Date 2019-03-08 10:45
 **/
@Aspect
@Component
public class AuthenticationAspect {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationAspect.class);

    @Autowired
    private IAuthAccessTokenService authAccessTokenService;

    /**
     * 配置织入点
     */
    @Pointcut("@annotation(com.ruoyi.common.annotation.ValidateAccessToken)")
    public void apiPointCut() {

    }


    /**
     * 前置通知 用于拦截操作
     *
     * @param joinPoint 切点
     */
    @Before("apiPointCut()")
    public void doBefore(JoinPoint joinPoint) throws Exception {
        handleValidate(joinPoint);
    }


    /**
     * 请求验证处理
     *
     * @param joinPoint
     */

    protected void handleValidate(final JoinPoint joinPoint) throws Exception {

        try {
            HttpServletRequest request = ServletUtils.getRequest();

            // 重复请求过滤（ip+url）
            // String ip = IpUtils.getIpAddr(request);
            // String url = request.getRequestURL().toString();
            // String tempkey = (ip + "#" + url).replaceAll(":", "");
            // if (!validateRequest(tempkey)) {
            //     throw new ApiRuntimeException(ResponseCode.RE_REQUEST);
            // }

            // 获得注解
            ValidateAccessToken annotation = getAnnotationLog(joinPoint);
            // 判断是否需要验证
            if (annotation == null || !annotation.required()) {
                return;
            }
            String accessToken = request.getParameter("access_token");

            if (StringUtils.isNoneBlank(accessToken)) {
                //查询数据库中的Access Token
                AuthAccessToken authAccessToken = authAccessTokenService.selectByAccessToken(accessToken);

                if (authAccessToken != null) {
                    Long savedExpiresAt = authAccessToken.getExpiresIn();
                    //过期日期
                    LocalDateTime expiresDateTime = DateUtils.ofEpochSecond(savedExpiresAt, null);
                    //当前日期
                    LocalDateTime nowDateTime = DateUtils.now();

                    //如果Access Token已经失效，则返回错误提示
                    if (expiresDateTime.isAfter(nowDateTime)) {
                        throw new ApiRuntimeException(ResponseCode.EXPIRED_TOKEN);
                    }
                    return;
                } else {
                    throw new ApiRuntimeException(ResponseCode.INVALID_GRANT);
                }
            } else {
                throw new ApiRuntimeException(ResponseCode.INVALID_REQUEST);
            }
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("接口请求异常:{}", exp.getMessage());
            throw exp;
        }
    }


    /**
     * 是否存在注解，如果存在就获取
     */
    private ValidateAccessToken getAnnotationLog(JoinPoint joinPoint) throws Exception {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null) {
            return method.getAnnotation(ValidateAccessToken.class);
        }
        return null;
    }


    /**
     * 是否拒绝服务 (5秒内>5次)
     *
     * @return
     */
    private boolean validateRequest(String key) {
        long count = JedisUtils.setIncr(key, 5);
        if (count > 5) {
            return false;
        }
        return true;
    }


}