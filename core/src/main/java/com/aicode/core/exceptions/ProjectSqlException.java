/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class ProjectSqlException extends BaseException implements Serializable {
    public ProjectSqlException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ProjectSqlException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ProjectSqlException(String message) {
        super(message);
    }

    public ProjectSqlException(String message, Throwable cause) {
        super(message, cause);
    }
}
