/*
 * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于AI-Code.
 */

package ${basePackage}.core.tools.excel;

import org.apache.poi.hssf.util.HSSFColor;

import java.lang.annotation.*;

/**
 * 自定义excel注解
 * Created by lixin on 2017/6/16.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelColumn {
    /**
     * 列名称
     *
     * @return
     */
    String value() default "";

    /**
     * 从1开始
     *
     * @return
     */
    int col() default 0;

    /**
     * 宽度
     *
     * @return
     */
    int width() default 20;

    /**
     * 字体颜色，默认黑色
     * 统一使用 HSSFColor类进行颜色传递
     *
     * @return
     */
    int color() default HSSFColor.BLACK.index;

    /**
     * 注释
     *
     * @return
     */
    String comment() default "";
}
