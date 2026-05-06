/*
* demo
 */
package com.aicode.exceptions;

import java.io.Serializable;

public class FrameworksTemplateException extends com.aicode.core.BaseException implements Serializable {
    public FrameworksTemplateException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public FrameworksTemplateException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public FrameworksTemplateException(String message) {
        super(message);
    }

    public FrameworksTemplateException(String message, Throwable cause) {
        super(message, cause);
    }
}
