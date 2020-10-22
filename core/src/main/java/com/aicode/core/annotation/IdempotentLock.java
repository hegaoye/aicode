package com.aicode.core.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 幂等锁，用于分布式共享资源锁定注解，
 * 或者幂等方法处理注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IdempotentLock {
    /**
     * 锁的缓存key，用于redis缓存，确保唯一不重复
     */
    String key() default "";

    /**
     * 自动过期时间默认 3s，可以根据实际业务复杂度确认一个较为合理的过期时间
     * 进行强制释放锁，避免死锁效应
     */
    long timeout() default 3;

    /**
     * 有效时间 单位，默认为秒，可以根据需要传递需要的值
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}