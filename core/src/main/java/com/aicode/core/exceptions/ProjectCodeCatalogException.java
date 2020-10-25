/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class ProjectCodeCatalogException extends BaseException implements Serializable {
    public ProjectCodeCatalogException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ProjectCodeCatalogException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ProjectCodeCatalogException(String message) {
        super(message);
    }

    public ProjectCodeCatalogException(String message, Throwable cause) {
        super(message, cause);
    }
}
