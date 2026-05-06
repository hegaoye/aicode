/*
* demo
 */
package com.aicode.exceptions;

import java.io.Serializable;

public class ProjectJobException extends com.aicode.core.BaseException implements Serializable {
    public ProjectJobException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ProjectJobException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ProjectJobException(String message) {
        super(message);
    }

    public ProjectJobException(String message, Throwable cause) {
        super(message, cause);
    }
}
