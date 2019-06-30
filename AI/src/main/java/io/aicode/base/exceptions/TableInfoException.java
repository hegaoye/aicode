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

public class TableInfoException extends BaseException {
    public TableInfoException(BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public TableInfoException(BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public TableInfoException(String message) {
        super(message);
    }

    public TableInfoException(String message, Throwable cause) {
        super(message, cause);
    }
}
