/*
* demo
 */
package com.aicode.exceptions;

import java.io.Serializable;

public class ProjectRepositoryAccountException extends com.aicode.core.BaseException implements Serializable {
    public ProjectRepositoryAccountException(BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ProjectRepositoryAccountException(BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ProjectRepositoryAccountException(String message) {
        super(message);
    }

    public ProjectRepositoryAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
