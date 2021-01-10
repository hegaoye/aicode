/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class DisplayAttributeException extends BaseException implements Serializable {
    public DisplayAttributeException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public DisplayAttributeException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public DisplayAttributeException(String message) {
        super(message);
    }

    public DisplayAttributeException(String message, Throwable cause) {
        super(message, cause);
    }
}
