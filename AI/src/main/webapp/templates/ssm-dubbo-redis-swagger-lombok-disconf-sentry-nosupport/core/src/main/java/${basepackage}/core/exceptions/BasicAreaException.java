package ${basePackage}.core.exceptions;

public class BasicAreaException extends BaseException {

    public BasicAreaException(BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public BasicAreaException(BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public BasicAreaException(String message) {
        super(message);
    }

    public BasicAreaException(String message, Throwable cause) {
        super(message, cause);
    }
}
