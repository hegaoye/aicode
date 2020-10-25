/*
* AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.core.exceptions;

import java.io.Serializable;

public class WorkerNodeException extends BaseException implements Serializable {
    public WorkerNodeException(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public WorkerNodeException(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public WorkerNodeException(String message) {
        super(message);
    }

    public WorkerNodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
