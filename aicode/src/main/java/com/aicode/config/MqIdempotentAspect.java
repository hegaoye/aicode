package com.aicode.config;

import com.aicode.core.annotation.IdempotentLock;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * mq的幂等切面拦截
 * 实现对消息队列的自动幂等处理，需要配合在需要的方法上的
 *
 * @see IdempotentLock 注解完成，具体的定义可以查看
 * {@link IdempotentLock}的定义
 */
@Slf4j
@Aspect
@Component
@Order(100)
public class MqIdempotentAspect {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AnnotationResolver annotationResolver;

    /**
     * 设置切面切入点
     * 针对所有的MQ消费者进行切入
     */
    @Pointcut("execution(* com.aicode.*.mq.consumer..*(..))")
    public void idempotent() {
    }

    /**
     * 环绕切入
     * 解析注解，并实现加锁，自动释放锁功能
     */
    @Around(value = "idempotent() && @annotation(idempotentLock)")
    public Object doAround(ProceedingJoinPoint joinPoint, IdempotentLock idempotentLock) throws Exception {
        String key = annotationResolver.resolver(joinPoint, idempotentLock.key());
        String keyValue = getLock(key, idempotentLock.timeout(), idempotentLock.timeUnit());
        if (StringUtil.isNullOrEmpty(keyValue)) {
            //todo 返回值处理
            return null;
        }

        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            //todo 返回值处理
            return null;
        } finally {
            this.unLock(key, keyValue);
        }
    }


    /**
     * 加分布式锁
     *
     * @param key      缓存key也可以成为锁
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    private String getLock(String key, long timeout, TimeUnit timeUnit) {
        try {
            String value = UUID.randomUUID().toString();
            key = "Lock:";
            String finalKey = key;
            Boolean lockStat = stringRedisTemplate.execute((RedisCallback<Boolean>) connection ->
                    connection.set(finalKey.getBytes(Charset.forName("UTF-8")), value.getBytes(Charset.forName("UTF-8")),
                            Expiration.from(timeout, timeUnit), RedisStringCommands.SetOption.SET_IF_ABSENT));
            if (!lockStat) {
                // 获取锁失败。
                return null;
            }
            return value;
        } catch (Exception e) {
            log.error("获取分布式锁失败，key={}", key, e);
            return null;
        }
    }


    /**
     * 解锁，释放锁
     *
     * @param key   锁的key
     * @param value 锁的值
     */
    private void unLock(String key, String value) {
        try {
            key = "Lock:";
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            String finalKey = key;
            boolean unLockStat = stringRedisTemplate.execute((RedisCallback<Boolean>) connection ->
                    connection.eval(script.getBytes(), ReturnType.BOOLEAN, 1,
                            finalKey.getBytes(Charset.forName("UTF-8")), value.getBytes(Charset.forName("UTF-8"))));
            if (!unLockStat) {
                log.error("释放分布式锁失败，key={}，已自动超时，其他线程可能已经重新获取锁", key);
            }
        } catch (Exception e) {
            log.error("释放分布式锁失败，key={}", key, e);
        }
    }
}
