package com.jihai.bitfree.aspect;


import com.alibaba.fastjson.JSON;
import com.jihai.bitfree.ability.MonitorAbility;
import com.jihai.bitfree.service.StatisticService;
import com.jihai.bitfree.utils.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Aspect
@Component
@Order(0)
@Slf4j
public class RequestLogAspect {

    @Autowired
    private RequestUtils requestUtils;

    @Autowired
    private MonitorAbility monitorAbility;

    @Around("execution(* com.jihai.bitfree.controller..*.*(..))")
    public Object process(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            statisticRequest();
            Signature signature = proceedingJoinPoint.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            Object[] args = proceedingJoinPoint.getArgs();
            String methodName = proceedingJoinPoint.getTarget().getClass().getName() + "." + methodSignature.getName();

            StringBuffer requestLog = new StringBuffer();
            requestLog.append(String.format("\n %s request to %s", requestUtils.getCurrentIp(), methodName));

            if (args.length > 0) {
                StringBuffer paramsBuffer = new StringBuffer();
                for (Object param : args) {
                    if (param instanceof MultipartFile) {
                        continue;
                    }
                    paramsBuffer.append(JSON.toJSONString(param)).append(",");
                }
                if (paramsBuffer.length() > 0) {
                    requestLog.append("\n params -> " + paramsBuffer.substring(0, paramsBuffer.length() - 1));
                }
            }

            // 理论上，这是最外层的切面，不可能抛异常
            long startTimestamp = System.currentTimeMillis();
            Object returnObj = proceedingJoinPoint.proceed();

            long costTime = System.currentTimeMillis() - startTimestamp;
            if (costTime > 200) {
                monitorAbility.sendMsg("耗时接口-> " + costTime + "ms, requestLog " + requestLog, Integer.MAX_VALUE);
            }

            if (returnObj != null) {
                requestLog.append("\n result -> " + JSON.toJSONString(returnObj) + " cost:" + costTime + "ms");
            }
            log.info(requestLog.toString());
            return returnObj;
        } catch (Throwable e) {
            log.error("RequestLogAspect ERROR", e);
            // 兜底
            return proceedingJoinPoint.proceed();
        }
    }

    @Autowired
    private StatisticService statisticService;

    private void statisticRequest() {
        statisticService.recordRequest();
    }
}
