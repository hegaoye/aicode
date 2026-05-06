/*
* demo
 */
package com.aicode.exceptions;

import java.io.Serializable;

public class WorkerNodeException extends com.aicode.core.BaseException implements Serializable {
    public WorkerNodeException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public WorkerNodeException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public WorkerNodeException(String message) {
        super(message);
    }

    public WorkerNodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
