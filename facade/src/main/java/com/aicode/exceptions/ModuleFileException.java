/*
* demo
 */
package com.aicode.exceptions;

import java.io.Serializable;

public class ModuleFileException extends com.aicode.core.BaseException implements Serializable {
    public ModuleFileException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ModuleFileException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ModuleFileException(String message) {
        super(message);
    }

    public ModuleFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
