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

public class TemplatesException extends BaseException {
    public TemplatesException(BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public TemplatesException(BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public TemplatesException(String message) {
        super(message);
    }

    public TemplatesException(String message, Throwable cause) {
        super(message, cause);
    }
}
