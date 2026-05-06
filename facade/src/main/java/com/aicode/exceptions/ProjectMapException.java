/*
* demo
 */
package com.aicode.exceptions;

import java.io.Serializable;

public class ProjectMapException extends com.aicode.core.BaseException implements Serializable {
    public ProjectMapException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ProjectMapException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ProjectMapException(String message) {
        super(message);
    }

    public ProjectMapException(String message, Throwable cause) {
        super(message, cause);
    }
}
