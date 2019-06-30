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

public class MoudlesException extends BaseException {
    public MoudlesException(BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public MoudlesException(BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public MoudlesException(String message) {
        super(message);
    }

    public MoudlesException(String message, Throwable cause) {
        super(message, cause);
    }
}
