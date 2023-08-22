package com.cointr.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class ExceptionAspect {

    @AfterThrowing(pointcut = "execution(* com.cointr..*(..))", throwing = "ex")
    public void handleException(Exception ex) {
        log.error("Exception caught: " + ex.getMessage());
    }
}

