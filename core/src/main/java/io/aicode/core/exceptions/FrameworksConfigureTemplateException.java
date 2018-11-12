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

public class FrameworksConfigureTemplateException extends BaseException {
    public FrameworksConfigureTemplateException(BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public FrameworksConfigureTemplateException(BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public FrameworksConfigureTemplateException(String message) {
        super(message);
    }

    public FrameworksConfigureTemplateException(String message, Throwable cause) {
        super(message, cause);
    }
}
