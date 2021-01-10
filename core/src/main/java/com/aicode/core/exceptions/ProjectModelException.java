/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class ProjectModelException extends BaseException implements Serializable {
    public ProjectModelException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ProjectModelException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ProjectModelException(String message) {
        super(message);
    }

    public ProjectModelException(String message, Throwable cause) {
        super(message, cause);
    }
}
