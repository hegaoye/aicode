/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class FrameworksTemplateException extends BaseException implements Serializable {
    public FrameworksTemplateException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public FrameworksTemplateException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public FrameworksTemplateException(String message) {
        super(message);
    }

    public FrameworksTemplateException(String message, Throwable cause) {
        super(message, cause);
    }
}
