package com.aicode.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动缓存枚举类型给前端使用
 * 仅用于枚举，否则无用
 * Created by d
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheEnum {
    /**
     * 枚举的编码
     *
     * @return 缓存用的编码
     */
    String value();
}