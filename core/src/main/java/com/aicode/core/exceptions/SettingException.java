/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class SettingException extends BaseException implements Serializable {
    public SettingException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public SettingException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public SettingException(String message) {
        super(message);
    }

    public SettingException(String message, Throwable cause) {
        super(message, cause);
    }
}
