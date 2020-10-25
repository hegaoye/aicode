/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class MapFieldColumnException extends BaseException implements Serializable {
    public MapFieldColumnException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public MapFieldColumnException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public MapFieldColumnException(String message) {
        super(message);
    }

    public MapFieldColumnException(String message, Throwable cause) {
        super(message, cause);
    }
}
