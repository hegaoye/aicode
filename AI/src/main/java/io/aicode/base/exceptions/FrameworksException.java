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

public class FrameworksException extends BaseException {
    public FrameworksException(BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public FrameworksException(BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public FrameworksException(String message) {
        super(message);
    }

    public FrameworksException(String message, Throwable cause) {
        super(message, cause);
    }
}
