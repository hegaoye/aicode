/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class ProjectJobLogsException extends BaseException implements Serializable {
    public ProjectJobLogsException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ProjectJobLogsException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ProjectJobLogsException(String message) {
        super(message);
    }

    public ProjectJobLogsException(String message, Throwable cause) {
        super(message, cause);
    }
}
