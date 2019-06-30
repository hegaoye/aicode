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
 * 登录异常类
 */
public class LoginException extends BaseException {

    public LoginException(BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public LoginException(BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public LoginException(String message) {
        super(message);
    }

    public LoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
