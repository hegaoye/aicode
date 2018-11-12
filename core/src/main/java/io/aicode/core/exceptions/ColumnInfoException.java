/*
 *
 *                        http://www.aicode.io
 *
 *
 *        本代码仅用于AI-Code.
 *
 */
package io.aicode.core.exceptions;

/**
 * 设置异常类
 *
 * @author lixin 2017-08-03 17:46
 */

public class ColumnInfoException extends BaseException {
    public ColumnInfoException(BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ColumnInfoException(BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ColumnInfoException(String message) {
        super(message);
    }

    public ColumnInfoException(String message, Throwable cause) {
        super(message, cause);
    }
}
