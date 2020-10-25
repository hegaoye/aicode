/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class MapRelationshipException extends BaseException implements Serializable {
    public MapRelationshipException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public MapRelationshipException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public MapRelationshipException(String message) {
        super(message);
    }

    public MapRelationshipException(String message, Throwable cause) {
        super(message, cause);
    }
}
