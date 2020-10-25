/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class AccountException extends BaseException implements Serializable {
    public AccountException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public AccountException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public AccountException(String message) {
        super(message);
    }

    public AccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
