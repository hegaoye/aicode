/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class ProjectModuleException extends BaseException implements Serializable {
    public ProjectModuleException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ProjectModuleException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ProjectModuleException(String message) {
        super(message);
    }

    public ProjectModuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
