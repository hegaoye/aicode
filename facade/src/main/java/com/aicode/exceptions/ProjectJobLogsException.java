/*
* demo
 */
package com.aicode.exceptions;

import java.io.Serializable;

public class ProjectJobLogsException extends com.aicode.core.BaseException implements Serializable {
    public ProjectJobLogsException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ProjectJobLogsException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ProjectJobLogsException(String message) {
        super(message);
    }

    public ProjectJobLogsException(String message, Throwable cause) {
        super(message, cause);
    }
}
