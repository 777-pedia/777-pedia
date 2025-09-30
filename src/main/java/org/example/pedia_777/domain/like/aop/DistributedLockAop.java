package org.example.pedia_777.domain.like.aop;

import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.pedia_777.domain.like.dto.response.LikeResponse;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@Order(0)
public class DistributedLockAop {
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(DistributedLock)")
    public void lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = REDISSON_LOCK_PREFIX + CustomELParser.getDynamicValue(signature.getParameterNames(),
                joinPoint.getArgs(), distributedLock.key());
        RLock rLock = redissonClient.getLock(key);
        try {
            boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(),
                    distributedLock.timeUnit());

            if (!available) {
                log.info("락을 획들 할 수 없습니다. method: {}  key: {}", method.getName(), key);
                return;
            }
//            aopForTransaction.proceed(joinPoint);
            LikeResponse proceed = (LikeResponse) joinPoint.proceed();
            System.out.println(
                    "Thread.currentThread().getName() = " + Thread.currentThread().getName() + " , available = "
                            + available + ", proceed = " + proceed);
        } catch (Exception e) {
            throw new IllegalArgumentException("예외가 발생하였습니다.");

        } finally {
            if (rLock.isHeldByCurrentThread()) {
                System.out.println(
                        "Thread.currentThread().getName() = " + Thread.currentThread().getName() + " release");
                rLock.unlock();
            }
        }
    }
}
