/*
* demo
 */
package com.aicode.exceptions;

import java.io.Serializable;

public class AccountException extends com.aicode.core.BaseException implements Serializable {
    public AccountException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public AccountException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public AccountException(String message) {
        super(message);
    }

    public AccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
