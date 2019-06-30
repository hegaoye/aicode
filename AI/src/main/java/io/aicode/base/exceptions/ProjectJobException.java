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

public class ProjectJobException extends BaseException {
    public ProjectJobException(BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ProjectJobException(BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ProjectJobException(String message) {
        super(message);
    }

    public ProjectJobException(String message, Throwable cause) {
        super(message, cause);
    }
}
