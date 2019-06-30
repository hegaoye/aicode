/*
 *
 *                       http://www.aicode.io
 *
 *
 *      本代码仅用于AI-Code.
 */
package io.aicode.base.ex;


import io.aicode.base.exceptions.BaseException;

/**
 * 基础数据配置异常
 *
 * @author lixin
 *         公寓方异常
 */
public class ScheduleException extends BaseException {
    public ScheduleException(BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ScheduleException(BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ScheduleException(String message) {
        super(message);
    }

    public ScheduleException(String message, Throwable cause) {
        super(message, cause);
    }
}
