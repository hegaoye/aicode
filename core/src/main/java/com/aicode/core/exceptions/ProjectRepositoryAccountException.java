/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class ProjectRepositoryAccountException extends BaseException implements Serializable {
    public ProjectRepositoryAccountException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ProjectRepositoryAccountException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ProjectRepositoryAccountException(String message) {
        super(message);
    }

    public ProjectRepositoryAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
