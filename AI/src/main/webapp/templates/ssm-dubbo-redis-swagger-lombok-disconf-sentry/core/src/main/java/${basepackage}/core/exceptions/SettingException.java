package ${basePackage}.core.exceptions;

/**
 * 设置异常类
 *
 * @author lixin 2017-08-03 17:46
 */

public class SettingException extends BaseException {
    public SettingException(BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public SettingException(BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public SettingException(String message) {
        super(message);
    }

    public SettingException(String message, Throwable cause) {
        super(message, cause);
    }
}
