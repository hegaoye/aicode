/*
* demo
 */
package com.aicode.exceptions;

import java.io.Serializable;

public class DisplayAttributeException extends com.aicode.core.BaseException implements Serializable {
    public DisplayAttributeException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public DisplayAttributeException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public DisplayAttributeException(String message) {
        super(message);
    }

    public DisplayAttributeException(String message, Throwable cause) {
        super(message, cause);
    }
}
