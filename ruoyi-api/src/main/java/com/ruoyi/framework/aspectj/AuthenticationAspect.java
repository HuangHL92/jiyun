/*
package com.ruoyi.framework.aspectj;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import com.ruoyi.common.annotation.ValidateRequest;
import com.ruoyi.common.base.ApiConst;
import com.ruoyi.common.enums.ResponseCode;
import com.ruoyi.common.exception.ApiRuntimeException;
import com.ruoyi.common.utils.IpUtils;
import com.ruoyi.common.utils.JedisUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

*/
/**
 * @Description RestfulApi 接口验证切面
 * @Author yufei
 * @Date 2019-03-08 10:45
 **//*


@Aspect
@Component
public class AuthenticationAspect {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationAspect.class);

    @Value("${api.appsecret}")
    private String appsecret;


    // 配置织入点
//    @Pointcut("@annotation(com.ruoyi.common.annotation.ValidateRequest)")
    @Pointcut("within(com.ruoyi.api..*)")
    public void apiPointCut()
    {

    }


    */
/**
     * 前置通知 用于拦截操作
     *
     * @param joinPoint 切点
     *//*

    @AfterReturning(pointcut = "apiPointCut()")
    public void doBefore(JoinPoint joinPoint) throws Exception {
        handleValidate(joinPoint, null);
    }


    */
/**
     * 拦截异常操作
     *
     * @param joinPoint
     * @param e
     *//*

    @AfterThrowing(value = "apiPointCut()", throwing = "e")
    public void doAfter(JoinPoint joinPoint, Exception e) throws Exception {
        handleValidate(joinPoint, e);
    }




    */
/**
     * 请求验证处理
     * @param joinPoint
     * @param e
     *//*

    protected void handleValidate(final JoinPoint joinPoint, final Exception e) throws Exception {

        try
        {
            HttpServletRequest request = ServletUtils.getRequest();

            // 重复请求过滤（ip+url）
            String ip = IpUtils.getIpAddr(request);
            String url = request.getRequestURL().toString();
            String tempkey = ( ip + "#" + url).replaceAll(":","");
            if(!validateRequest(tempkey)) {
                throw new ApiRuntimeException(ResponseCode.RE_REQUEST);
            }

            // 获得注解
            ValidateRequest annotation = getAnnotationLog(joinPoint);
            // 判断是否需要验证
            if (annotation == null || !annotation.required())
            {
                return;
            }
            //从 http 请求头中取出 token
            String token = request.getHeader("x-access-token");

            // 执行认证
            if (StringUtils.isEmpty(token)) {
                throw new ApiRuntimeException(ResponseCode.ILLEGAL_REQUEST);
            }

            // 获取 token 中的 user id
            String cahce_key;
            try {
                cahce_key = JWT.decode(token).getAudience().get(0);

                //取得缓存中的token并验证
                String token_cahce  =JedisUtils.get(String.format(ApiConst.TOKEN_KEY,cahce_key));
                if(token_cahce==null || !token.equals(token_cahce)) {
                    throw new ApiRuntimeException(ResponseCode.EXPIRE_TOKEN);
                }

            } catch (JWTDecodeException j) {
                throw new ApiRuntimeException(ResponseCode.ILLEGAL_REQUEST);
            }
//            User user = userService.findUserById(cahce_key);
//            if (user == null) {
//                throw new ApiRuntimeException(ResponseCode.ILLEGAL_ACCOUNT);
//            }

            //TODO 密码用于验证token是否有效。可以提供两种实现方式（1：全系统用一个密码 2：跟着每个账号走）
            // 验证 token
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(appsecret)).build();
            try {
                jwtVerifier.verify(token);
            } catch (JWTVerificationException ex) {
                throw new ApiRuntimeException(ResponseCode.ILLEGAL_TOKEN);
            }

        }
        catch (Exception exp)
        {
            // 记录本地异常日志
            log.error("接口请求异常:{}", exp.getMessage());
            throw exp;
        }
    }


    */
/**
     * 是否存在注解，如果存在就获取
     *//*

    private ValidateRequest getAnnotationLog(JoinPoint joinPoint) throws Exception
    {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null)
        {
            return method.getAnnotation(ValidateRequest.class);
        }
        return null;
    }


    */
/**
     * 是否拒绝服务 (5秒内>5次)
     * @return
     *//*

    private boolean validateRequest(String key){
        long count=JedisUtils.setIncr(key, 5);
        if(count>5){
            return false;
        }
        return true;
    }


}
*/
