package com.devloop.aop;

import com.devloop.common.apipayload.status.ErrorStatus;
import com.devloop.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.util.Arrays;

@Component
@Aspect
@Slf4j(topic = "서비스 레이어 AOP")
public class LogAspect {

    @Pointcut("execution(* com.devloop..service..*.*(..))")
    private void serviceLayer(){};

    @Around("serviceLayer()")
    public Object serviceLogExecution(ProceedingJoinPoint joinPoint) throws Throwable{
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.info("{}.{} 메서드 실행 시작", className,method.getName());
        log.info("메서드 파라미터 : {}", Arrays.toString(joinPoint.getArgs()));

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = null;
        try{
            result = joinPoint.proceed();
            stopWatch.stop();

            log.info("{}.{} 메소드 실행 완료", className, method.getName());
            log.info("실행 시간: {}ms", stopWatch.getTotalTimeMillis());
            log.info("반환 값: {}", result != null ? result.toString() : "없음");

            return result;
        } catch(Exception e){
            log.error("{}.{} 메서드 실행 중 오류 발생 : {} - {}",className, method.getName(),e.getClass().getSimpleName(),e.getMessage());
            throw new ApiException(ErrorStatus._METHOD_RUN_ERROR);
        }
    }
}
