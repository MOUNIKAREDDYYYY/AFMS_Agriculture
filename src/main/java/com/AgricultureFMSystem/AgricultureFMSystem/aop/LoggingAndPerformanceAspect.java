package com.AgricultureFMSystem.AgricultureFMSystem.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * ============================================================================================
 * Phase 1: Spring AOP (Aspect-Oriented Programming) Component
 * ============================================================================================
 * Purpose: Cross-cutting concern implementation for performance tracking,
 * audit logging,
 * and error tracing across the Agricultural Farm Management Service and
 * Controller layers.
 *
 * Concepts demonstrated:
 * - @Aspect: Marks this class as an AOP Aspect containing cross-cutting
 * pointcuts and advices
 * - @Pointcut: Expression defining where advice should be applied
 * (JoinPoints)
 * - @Around: Intercepts method execution before and after to measure
 * duration and profile performance
 * - @Before: Executed prior to method entry for request payload and audit
 * logging
 * - @AfterThrowing: Intercepts unhandled exceptions for central audit
 * logging before reaching @RestControllerAdvice
 * ============================================================================================
 */
@Slf4j
@Aspect
@Component
public class LoggingAndPerformanceAspect {

    /**
     * Pointcut 1: Targets all methods inside the service layer
     */
    @Pointcut("within(com.AgricultureFMSystem.AgricultureFMSystem.service..*)")
    public void serviceLayerPointcut() {
        // Pointcut signature - method body is empty
    }

    /**
     * Pointcut 2: Targets all methods inside REST controllers
     */
    @Pointcut("within(com.AgricultureFMSystem.AgricultureFMSystem.controller..*)")
    public void controllerLayerPointcut() {
        // Pointcut signature - method body is empty
    }

    /**
     * Advice 1: @Around advice to profile service method execution time and log
     * slow queries/operations
     * Parameters: ProceedingJoinPoint allows controlling the method invocation
     * pipeline
     */
    @Around("serviceLayerPointcut()")
    public Object profileServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();

        try {
            // Proceed with actual method execution
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            // Performance auditing: Flag operations taking more than 500ms
            if (executionTime > 500) {
                log.warn("[AOP Performance] Slow execution in {}.{}() took {} ms",
                        className, methodName, executionTime);
            } else {
                log.debug("[AOP Profile] {}.{}() completed in {} ms", className, methodName, executionTime);
            }

            return result;
        } catch (Throwable ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("[AOP Execution Failure] {}.{}() failed after {} ms with error: {}",
                    className, methodName, executionTime, ex.getMessage());
            throw ex;
        }
    }

    /**
     * Advice 2: @Before advice to log incoming controller requests
     */
    @Before("controllerLayerPointcut()")
    public void logControllerEntry(JoinPoint joinPoint) {
        String controllerName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("[AOP Audit] HTTP Request entering {}.{}() with arguments count: {}",
                controllerName, methodName, args != null ? args.length : 0);
    }

    /**
     * Advice 3: @AfterThrowing advice to capture and log service exceptions for
     * enterprise diagnostics
     */
    @AfterThrowing(pointcut = "serviceLayerPointcut()", throwing = "exception")
    public void logServiceException(JoinPoint joinPoint, Throwable exception) {
        log.error("[AOP Exception Tracker] Exception thrown in method: {}.{}() - Exception Type: {} | Message: {}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                exception.getClass().getSimpleName(),
                exception.getMessage());
    }
}