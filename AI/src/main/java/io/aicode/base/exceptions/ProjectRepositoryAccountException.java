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

public class ProjectRepositoryAccountException extends BaseException {
    public ProjectRepositoryAccountException(BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ProjectRepositoryAccountException(BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ProjectRepositoryAccountException(String message) {
        super(message);
    }

    public ProjectRepositoryAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
