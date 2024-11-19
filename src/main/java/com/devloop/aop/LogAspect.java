package com.devloop.aop;

import com.devloop.common.enums.Category;
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

    @Pointcut("execution(* com.devloop..service..*.*(..)) " +
            "&& !@annotation(org.springframework.context.event.EventListener) " +
            "&& !@annotation(jakarta.annotation.PostConstruct) " +
            "&& !execution(* *.initializeElasticsearchData(..))")
    private void serviceLayer(){}

    @Pointcut("execution(* com.devloop..service..*(com.devloop.common.enums.Category, ..))")
    private void categoryPointcut(){}

    @Around("serviceLayer()")
    public Object serviceLogExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();

        // 초기화 관련 메서드 제외
        if (methodName.contains("initialize") || methodName.contains("sync")) {
            return joinPoint.proceed();
        }

        log.info("{}.{} 메서드 실행 시작", className, methodName);
        log.info("메서드 파라미터 : {}", Arrays.toString(joinPoint.getArgs()));

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = null;
        try {
            result = joinPoint.proceed();
            stopWatch.stop();

            log.info("{}.{} 메소드 실행 완료", className, methodName);
            log.info("실행 시간: {}ms", stopWatch.getTotalTimeMillis());
            if (result != null) {
                log.info("반환 값: {}", result);
            }

            return result;
        } catch (Exception e) {
            log.error("{}.{} 메서드 실행 중 오류 발생 : {} - {}",
                    className, methodName,
                    e.getClass().getSimpleName(), e.getMessage());
            throw e; // 원래 예외를 그대로 throw
        }
    }

    @Around("categoryPointcut()")
    public Object categoryLogExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Category category = null;

        for (Object arg : args) {
            if (arg instanceof Category) {
                category = (Category) arg;
                break;
            }
        }

        Object result = joinPoint.proceed();

        if (category != null) {
            switch (category) {
                case WEB_DEV -> log.info("웹 개발 카테고리로 변경 되었습니다.");
                case APP_DEV -> log.info("앱 개발 카테고리로 변경 되었습니다");
                case GAME_DEV -> log.info("게임 개발 카테고리로 변경되었습니다");
                case ETC -> log.info("기타 카테고리로 변경 되었습니다.");
            }
        }
        return result;
    }
}