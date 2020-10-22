/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class ModuleFileException extends BaseException implements Serializable {
    public ModuleFileException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ModuleFileException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ModuleFileException(String message) {
        super(message);
    }

    public ModuleFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
