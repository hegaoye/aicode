/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class ProjectFramworkException extends BaseException implements Serializable {
    public ProjectFramworkException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ProjectFramworkException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ProjectFramworkException(String message) {
        super(message);
    }

    public ProjectFramworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
