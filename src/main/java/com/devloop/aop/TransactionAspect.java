package com.devloop.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j(topic = "트랜잭션 AOP")
public class TransactionAspect {

    @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
    private void transactionalAnnotationMethod() {
    }

    @Around("transactionalAnnotationMethod()")
    public Object transactionStatus(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        boolean isTransactionalActive = TransactionSynchronizationManager.isActualTransactionActive();
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

        log.info(" 트랜잭션 시작 {}.{} (활성화 = {}, 읽기 전용 = {}", className, method.getName(), isTransactionalActive, isReadOnly);

        try {
            Object result = joinPoint.proceed();

            if (isTransactionalActive) {
                log.info(" 트랜잭션 커밋 {}.{} 메서드 정상 처리됨", className, method.getName());
            }
            return result;
        } catch (Exception e) {
            if (isTransactionalActive) {
                log.error(" 트랜잭션 롤백 {}.{} 메서드 처리 중 오류 발생 : {} - {}", className, method.getName(), e.getClass().getSimpleName(), e.getMessage());
            }
            throw e;
        } finally {
            if (isTransactionalActive) {
                log.info(" 트랜잭션 종료 {}. {} 메서드 처리 완료", className, method.getName());
            }
        }
    }
}
