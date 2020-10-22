/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class MapClassTableException extends BaseException implements Serializable {
    public MapClassTableException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public MapClassTableException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public MapClassTableException(String message) {
        super(message);
    }

    public MapClassTableException(String message, Throwable cause) {
        super(message, cause);
    }
}
