package cn.xiaoniu.cloud.server.web.log;

import cn.hutool.core.map.MapUtil;
import cn.xiaoniu.cloud.server.util.JsonUtil;
import cn.xiaoniu.cloud.server.util.RegexUtil;
import cn.xiaoniu.cloud.server.util.constant.CommonConstant;
import cn.xiaoniu.cloud.server.util.exception.UtilException;
import cn.xiaoniu.cloud.server.web.util.HttpServletRequestUtil;
import cn.xiaoniu.cloud.server.web.util.Log;
import cn.xiaoniu.cloud.server.web.util.MethodUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;

/**
 * @author 孔明
 * @date 2020/4/21 17:59
 * @description cn.xiaoniu.cloud.server.web.log.PrintLogAspect
 */
@Aspect
@Component
public class PrintLogAspect implements Ordered {

    /**
     * 执行顺序
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 1;
    }

    /**
     * 以自定义 @PrintLog 注解为切点
     */
    @Pointcut("@annotation(cn.xiaoniu.cloud.server.web.log.PrintLog)")
    public void printLog() {
        // 定义切点不需要方法体
    }

    /**
     * 在切点之前织入
     *
     * @param joinPoint
     */
    @Before("printLog()")
    public void doBefore(JoinPoint joinPoint) {
        try {
            // 开始打印请求日志
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            Log.info("-------------------- 请求日志 --------------------");
            Log.info("请求IP:{}", HttpServletRequestUtil.getClientIP(request));
            Log.info("请求地址:{}", HttpServletRequestUtil.getRequestURI(request));
            Log.info("请求方式:{}", HttpServletRequestUtil.getMethod(request));

            // 目标Class
            Class targetClass = joinPoint.getTarget().getClass();

            // 目标方法签名
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

            // 目标方法
            Method method = methodSignature.getMethod();

            // 获取@PrintLog注解的接口描述信息
            PrintLog printLog = method.getAnnotation(PrintLog.class);
            if (printLog != null && StringUtils.isNotBlank(printLog.description())) {
                Log.info("请求方法:{}", printLog.description());
            }

            Log.info("请求方法路径:{}.{}()", targetClass.getName(), method.getName());
            Log.info("请求参数:{}", MethodUtil.getInstance().getJSONParams(methodSignature, joinPoint.getArgs()));

        } catch (Exception ex) {
            throw new UtilException(ex, "使用AOP打印请求参数异常！");
        }
    }

    /**
     * 切点环绕织入
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("printLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        Log.info("请求返回:{}", JsonUtil.toJson(result));
        Log.info("请求耗时:{}\n", System.currentTimeMillis() - startTime);
        return result;
    }
}
