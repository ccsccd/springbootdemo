package org.redrock.springbootdemo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redrock.springbootdemo.annotation.Cache;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
@Order(1)
public class CacheAspect {

    private Map<String, Object> cacheMap = new HashMap<>();
    //该缓存时间不能针对参数不同而不同
    private long lastTime = 0;
    private long cacheTime = 30 * 60 * 1000;

    @Pointcut("execution(public * org.redrock.springbootdemo.controller.*.*(..))")
    public void cache() {
    }

    @Around("cache()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("aop2-----around 1");
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Cache isCache = method.getDeclaredAnnotation(Cache.class);
        if (isCache != null && (System.currentTimeMillis() - lastTime <= cacheTime)) {
            String key = method.getName() + "/" + Arrays.toString(pjp.getArgs());
            Object value = cacheMap.get(key);
            if (value != null) {
                return value;
            }
        }
        System.out.println("aop2-----around 2");
        return pjp.proceed();
    }

    @AfterReturning(value = "cache()", returning = "object")
    public void after(JoinPoint joinPoint, Object object) {
        System.out.println("aop2-----afterReturning");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Cache isCache = method.getDeclaredAnnotation(Cache.class);
        if (isCache != null) {
            String key = method.getName() + "/" + Arrays.toString(joinPoint.getArgs());
            lastTime = System.currentTimeMillis();
            cacheMap.putIfAbsent(key, object);
        }
    }

}
