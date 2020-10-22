/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class FrameworksException extends BaseException implements Serializable {
    public FrameworksException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public FrameworksException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public FrameworksException(String message) {
        super(message);
    }

    public FrameworksException(String message, Throwable cause) {
        super(message, cause);
    }
}
