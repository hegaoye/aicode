/*
 * Copyright (c) 2017. 郑州三楂红科技有限公司.保留所有权利.
 *          郑州三楂红科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定业务使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于三楂红商城系统.
 */

package com.rzhkj.base.annotantion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 忽略某个枚举值时使用
 * Created by lixin on 2017/8/23.
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore {
    String value() default "";
}
