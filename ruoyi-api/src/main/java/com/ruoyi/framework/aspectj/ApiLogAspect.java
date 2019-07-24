package com.ruoyi.framework.aspectj;

import cn.hutool.core.date.DateTime;
import com.google.gson.Gson;
//import com.ruoyi.common.utils.ServletUtils;
//import com.ruoyi.common.utils.StringUtils;
//import com.ruoyi.common.utils.http.HttpUtils;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;

/**
 * 接口访问日志记录处理
 * 
 * @author ruoyi
 */
@Aspect
@Component
public class ApiLogAspect
{

    private static final Logger log = LoggerFactory.getLogger("restful_api");

    // 开始时间
    ThreadLocal<Long> startTime = new ThreadLocal<>();


    /***
     * 配置织入点
     */
    @Pointcut("within(com.ruoyi.api..*)")
    public void logPointCut()
    {
    }

    /**
     * 前置通知 用于拦截操作
     *
     * @param joinPoint 切点
     */
    @Before("logPointCut()")
    public void doBefore(JoinPoint joinPoint)
    {
        startTime.set(System.currentTimeMillis());

        handleLog(joinPoint, null);

        Object[] args = joinPoint.getArgs();
        MethodSignature signature  = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        log.info("{}.{}: 请求参数：{}",method.getDeclaringClass().getName(),method.getName(), StringUtils.join(args,";"));

    }

    /**
     * 执行后 用于拦截操作
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "logPointCut()",returning = "rvt")
    public void doAfter(JoinPoint joinPoint,Object rvt)
    {
        long end = System.currentTimeMillis();
        long total =end - startTime.get();
        startTime.remove();
        MethodSignature signature  = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        log.info("{}.{}: 耗时：{}毫秒 返回参数：{}",method.getDeclaringClass().getName(),method.getName(),total,new Gson().toJson(rvt));
    }

    /**
     * 拦截异常操作
     * 
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfter(JoinPoint joinPoint, Exception e)
    {

        MethodSignature signature  = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        log.error("{}.{}: 异常：{}",method.getDeclaringClass().getName(),method.getName(),e.toString());
    }


    protected void handleLog(final JoinPoint joinPoint, final Exception e)
    {
        try
        {
            StringBuffer sb = new StringBuffer();

//            HttpServletRequest request = ServletUtils.getRequest();
//            String agentString = request.getHeader("User-Agent");
//            UserAgent userAgent = UserAgent.parseUserAgentString(agentString);
//            OperatingSystem operatingSystem = userAgent.getOperatingSystem(); // 操作系统信息
//            eu.bitwalker.useragentutils.DeviceType deviceType = operatingSystem.getDeviceType(); // 设备类型
//            String url = request.getRequestURL().toString();
//            // 请求的地址
//            String ip = HttpUtils.getClientIP(request);
//            sb.append("\r\n").append("*******************************请求信息-S***************************").append("\r\n");
//            sb.append("URL：" + url).append("\r\n");
//            sb.append("IP：" + ip).append("\r\n");
//            sb.append("请求时间：" + DateTime.now().toString("\"yyyy年MM月dd日 hh时mm分ss秒\"")).append("\r\n");
//            sb.append("操作系统：" + operatingSystem.getName()).append("\r\n");
//            sb.append("设备类型：" + deviceType).append("\r\n");
//            sb.append("头部信息：").append("\r\n");
//            Enumeration<String> header =request.getHeaderNames();
//            while (header.hasMoreElements()){
//                String s = header.nextElement();
//                sb.append(s+":"+request.getHeader(s)).append("\r\n");
//            }
//            sb.append("*******************************请求信息-E***************************");
//            log.info(sb.toString());
        }
        catch (Exception exp)
        {
            // 记录本地异常日志
            log.error("==前置通知异常==");
            log.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }


}
