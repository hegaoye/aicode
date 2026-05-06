/*
* demo
 */
package com.aicode.exceptions;

import java.io.Serializable;

public class MapFieldColumnException extends com.aicode.core.BaseException implements Serializable {
    public MapFieldColumnException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public MapFieldColumnException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public MapFieldColumnException(String message) {
        super(message);
    }

    public MapFieldColumnException(String message, Throwable cause) {
        super(message, cause);
    }
}
