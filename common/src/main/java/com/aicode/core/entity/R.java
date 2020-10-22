package com.aicode.core.entity;

import com.aicode.core.exceptions.BaseException;
import lombok.Data;

import java.io.Serializable;

/**
 * 结果返回类
 */
@Data
public final class R implements Serializable {
    private String info = BaseException.BaseExceptionEnum.Server_Error.msg;
    private Object data = null;
    private String code = BaseException.BaseExceptionEnum.Server_Error.code;


    /**
     * 失败 false  传递 异常枚举对象
     */
    public static R failed(String code, String msg) {
        return new R(code, msg, null);
    }

    public static R failed(BaseException.BaseExceptionEnum baseExceptionEnum) {
        return new R(baseExceptionEnum.code, baseExceptionEnum.msg, null);
    }

    public static R failed(BaseException.BaseExceptionEnum baseExceptionEnum, Object data) {
        return new R(baseExceptionEnum.code, baseExceptionEnum.msg, data);
    }

    /**
     * 成功 true 成功 “异常” 对象信息
     *
     * @param baseExceptionEnum 成功 “异常” 对象信息
     */

    public static R success(BaseException.BaseExceptionEnum baseExceptionEnum, Object data) {
        return new R(baseExceptionEnum.code, baseExceptionEnum.msg, data);
    }

    public static R success(BaseException.BaseExceptionEnum baseExceptionEnum) {
        return new R(baseExceptionEnum.code, baseExceptionEnum.msg, null);
    }

    public static R success(Object data) {
        return new R(BaseException.BaseExceptionEnum.Success.code, BaseException.BaseExceptionEnum.Success.msg, data);
    }

    public static R success() {
        return new R(BaseException.BaseExceptionEnum.Success.code, BaseException.BaseExceptionEnum.Success.msg, null);
    }


    private R() {
    }

    private R(String code, String info, Object data) {
        this.info = info;
        this.data = data;
        this.code = code;
    }
}