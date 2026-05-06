/*
* demo
 */
package com.aicode.exceptions;

import java.io.Serializable;

public class ProjectCodeCatalogException extends com.aicode.core.BaseException implements Serializable {
    public ProjectCodeCatalogException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ProjectCodeCatalogException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ProjectCodeCatalogException(String message) {
        super(message);
    }

    public ProjectCodeCatalogException(String message, Throwable cause) {
        super(message, cause);
    }
}
