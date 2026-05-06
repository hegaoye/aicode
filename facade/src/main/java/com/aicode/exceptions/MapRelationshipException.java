/*
* demo
 */
package com.aicode.exceptions;

import java.io.Serializable;

public class MapRelationshipException extends com.aicode.core.BaseException implements Serializable {
    public MapRelationshipException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public MapRelationshipException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public MapRelationshipException(String message) {
        super(message);
    }

    public MapRelationshipException(String message, Throwable cause) {
        super(message, cause);
    }
}
