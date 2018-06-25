
package ${basePackage}.core.entity;


import ${basePackage}.core.exceptions.BaseException;
import lombok.Data;

import java.io.Serializable;

@Data
public class BeanRet implements Serializable {
    private boolean success = false;
    private String info = BaseException.BaseExceptionEnum.Server_Error.error;
    private Object data = null;
    private String code = BaseException.BaseExceptionEnum.Server_Error.errCode;


    public static BeanRet create() {
        return new BeanRet();
    }

    public static BeanRet create(String info) {
        return new BeanRet(false, info);
    }

    public static BeanRet create(BaseException.BaseExceptionEnum baseExceptionEnum) {
        return new BeanRet(false, baseExceptionEnum.errCode, baseExceptionEnum.error);
    }

    public static BeanRet create(boolean success, BaseException.BaseExceptionEnum baseExceptionEnum) {
        return new BeanRet(success, baseExceptionEnum.errCode, baseExceptionEnum.error);
    }

    public static BeanRet create(boolean success, BaseException.BaseExceptionEnum baseExceptionEnum, String info) {
        return new BeanRet(success, baseExceptionEnum.errCode, baseExceptionEnum.error, info);
    }

    public static BeanRet create(boolean success, BaseException.BaseExceptionEnum baseExceptionEnum, Object data) {
        return new BeanRet(success, baseExceptionEnum.errCode, baseExceptionEnum.error, data);
    }

    public static BeanRet create(boolean success, String info) {
        return new BeanRet(success, info);
    }

    public static BeanRet create(boolean success, String code, String info) {
        return new BeanRet(success, code, info);
    }

    public static BeanRet create(boolean success, String info, Object data) {
        return new BeanRet(success, info, data);
    }

    public static BeanRet create(boolean success, String code, String info, Object data) {
        return new BeanRet(success, code, info, data);
    }

    public static BeanRet create(boolean success, BaseException.BaseExceptionEnum baseExceptionEnum, String info, Object data) {
        return new BeanRet(success, baseExceptionEnum.errCode, info, data);
    }


    private BeanRet() {

    }

    private BeanRet(boolean success, String info) {
        this.success = success;
        this.info = info;
        if (success) {
            this.code = BaseException.BaseExceptionEnum.Success.errCode;
        }
    }


    public BeanRet(boolean success, String code, String info) {
        this.success = success;
        this.code = code;
        this.info = info;
    }

    private BeanRet(boolean success, String info, Object data) {
        this.success = success;
        this.info = info;
        this.data = data;
        if (success) {
            this.code = BaseException.BaseExceptionEnum.Success.errCode;
        }
    }

    public BeanRet(boolean success, String code, String info, Object data) {
        this.success = success;
        this.info = info;
        this.data = data;
        this.code = code;
    }
}
