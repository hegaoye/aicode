/*
 *
 *                        http://www.aicode.io
 *
 *
 *        本代码仅用于AI-Code.
 *
 */
package io.aicode.base.exceptions;

/**
 * 设置异常类
 *
 * @author lixin 2017-08-03 17:46
 */

public class TemplateCategoryException extends BaseException {
    public TemplateCategoryException(BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public TemplateCategoryException(BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public TemplateCategoryException(String message) {
        super(message);
    }

    public TemplateCategoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
