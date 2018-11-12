/*
 *
 *                       http://www.aicode.io
 *
 *
 *      本代码仅用于AI-Code.
 */

package io.aicode.core.tools.excel;

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
