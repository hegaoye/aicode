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

public class ToolsCategoryException extends BaseException {
    public ToolsCategoryException(BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ToolsCategoryException(BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ToolsCategoryException(String message) {
        super(message);
    }

    public ToolsCategoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
