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

public class ToolsException extends BaseException {
    public ToolsException(BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ToolsException(BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ToolsException(String message) {
        super(message);
    }

    public ToolsException(String message, Throwable cause) {
        super(message, cause);
    }
}
