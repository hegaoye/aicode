/*
* demo
 */
package com.aicode.exceptions;

import java.io.Serializable;

public class FrameworksException extends com.aicode.core.BaseException implements Serializable {
    public FrameworksException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public FrameworksException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public FrameworksException(String message) {
        super(message);
    }

    public FrameworksException(String message, Throwable cause) {
        super(message, cause);
    }
}
