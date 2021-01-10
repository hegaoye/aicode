/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class ProjectJobException extends BaseException implements Serializable {
    public ProjectJobException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ProjectJobException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ProjectJobException(String message) {
        super(message);
    }

    public ProjectJobException(String message, Throwable cause) {
        super(message, cause);
    }
}
