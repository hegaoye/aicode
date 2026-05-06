/*
* demo
 */
package com.aicode.exceptions;

import java.io.Serializable;

public class ModuleException extends com.aicode.core.BaseException implements Serializable {
    public ModuleException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ModuleException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ModuleException(String message) {
        super(message);
    }

    public ModuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
