/*
 *
 *                       http://www.aicode.io
 *
 *
 *      本代码仅用于AI-Code.
 */
package io.aicode.base.ex;


import io.aicode.core.exceptions.BaseException;

/**
 * 基础数据配置异常
 *
 * @author lixin
 *         公寓方异常
 */
public class JobException extends BaseException {


    public JobException(BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public JobException(BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public JobException(String message) {
        super(message);
    }

    public JobException(String message, Throwable cause) {
        super(message, cause);
    }
}
