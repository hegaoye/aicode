/*
* demo
 */
package com.aicode.exceptions;

import java.io.Serializable;

public class MapClassTableException extends com.aicode.core.BaseException implements Serializable {
    public MapClassTableException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public MapClassTableException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public MapClassTableException(String message) {
        super(message);
    }

    public MapClassTableException(String message, Throwable cause) {
        super(message, cause);
    }
}
