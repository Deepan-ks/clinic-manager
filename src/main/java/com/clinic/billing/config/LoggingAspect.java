package com.clinic.billing.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

    private final ObjectMapper mapper;

    @Pointcut("within(com.clinic.billing.controller..*)")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        log.info("Incoming Request: [{}] {} - Method: {}.{}()",
                request.getMethod(),
                request.getRequestURI(),
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());

        if (log.isDebugEnabled()) {
            log.debug("Request Args: {}", mapper.writeValueAsString(joinPoint.getArgs()));
        }

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", e.getMessage(), joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e;
        } catch (Exception e) {
            log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL");
            throw e;
        }

        long elapsedTime = System.currentTimeMillis() - start;

        log.info("Outgoing Response: [{}] {} - Executed in {} ms",
                request.getMethod(),
                request.getRequestURI(),
                elapsedTime);

        if (log.isDebugEnabled()) {
            log.debug("Response: {}", mapper.writeValueAsString(result));
        }

        return result;
    }
}
