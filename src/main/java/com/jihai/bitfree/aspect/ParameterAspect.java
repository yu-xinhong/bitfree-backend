package com.jihai.bitfree.aspect;


import com.alibaba.fastjson.JSON;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Order(3)
@Component
public class ParameterAspect {

    @Around("execution(* com.jihai.bitfree.controller..*.*(..))")
    public Object process(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        if (args.length > 0) {
            for (Object param : args) {
                Set<ConstraintViolation<Object>> checkResult = validator.validate(param);
                if (! CollectionUtils.isEmpty(checkResult)) {
                    List<String> resultMsg = checkResult.stream().map(e -> e.getMessage()).collect(Collectors.toList());
                    throw new RuntimeException(JSON.toJSONString(resultMsg));
                }
            }
        }
        return proceedingJoinPoint.proceed();

    }
}
