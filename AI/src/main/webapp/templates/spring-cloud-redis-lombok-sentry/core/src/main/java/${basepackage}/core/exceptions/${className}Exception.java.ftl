/*
 * ${copyright}
 */
package ${basePackage}.core.exceptions;

import java.io.Serializable;

public class ${className}Exception extends BaseException implements Serializable {
    public ${className}Exception(BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public ${className}Exception(BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public ${className}Exception(String message) {
        super(message);
    }

    public ${className}Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
