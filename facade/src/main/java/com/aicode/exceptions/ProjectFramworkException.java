/*
* demo
 */
package com.aicode.exceptions;

import java.io.Serializable;

public class ProjectFramworkException extends com.aicode.core.BaseException implements Serializable {
    public ProjectFramworkException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ProjectFramworkException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ProjectFramworkException(String message) {
        super(message);
    }

    public ProjectFramworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
