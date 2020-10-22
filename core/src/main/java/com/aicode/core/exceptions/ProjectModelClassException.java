/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class ProjectModelClassException extends BaseException implements Serializable {
    public ProjectModelClassException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ProjectModelClassException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ProjectModelClassException(String message) {
        super(message);
    }

    public ProjectModelClassException(String message, Throwable cause) {
        super(message, cause);
    }
}
