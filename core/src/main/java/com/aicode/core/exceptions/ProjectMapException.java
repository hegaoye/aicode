/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class ProjectMapException extends BaseException implements Serializable {
    public ProjectMapException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ProjectMapException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ProjectMapException(String message) {
        super(message);
    }

    public ProjectMapException(String message, Throwable cause) {
        super(message, cause);
    }
}
