package com.aicode.core;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应返回类。
 *
 * <p><b>为什么是泛型</b>：本类最初声明为 {@code private Object data}，导致 springdoc 导出的
 * OpenAPI 契约里 {@code R.data} 的 schema 为空对象（{@code {}}），
 * 前端无法从契约推导出任何字段级类型 —— 跨语言契约驱动因此完全失效。
 * 泛型化后 {@code T} 由各 Controller 方法的返回类型承载，契约才能给出真实结构。
 *
 * <p><b>用法</b>：类型由方法的<b>目标返回类型</b>驱动推断，调用点通常无需写类型见证：
 * <pre>
 * public R&lt;List&lt;ProjectVO&gt;&gt; list() {
 *     return R.success(projectService.list(...));   // T 推断为 List&lt;ProjectVO&gt;
 * }
 * </pre>
 *
 * <p><b>泛型化不改变运行期 JSON 结构</b> —— 泛型会被擦除，{@code data} 字段的运行期类型
 * 仍是 {@code Object}。该不变量由 {@code ApiContractIT} 的响应结构断言守护。
 *
 * @param <T> {@code data} 字段的类型。无数据接口用 {@code Void}。
 */
@Data
public final class R<T> implements Serializable {
    private String info = BaseException.BaseExceptionEnum.Server_Error.msg;
    private T data = null;
    private String code = BaseException.BaseExceptionEnum.Server_Error.code;
    private boolean success = false;


    /**
     * 失败 false  传递 异常枚举对象
     */
    public static <T> R<T> failed(String code, String msg) {
        return new R<>(code, msg, null);
    }

    public static <T> R<T> failed(BaseException.BaseExceptionEnum baseExceptionEnum) {
        return new R<>(baseExceptionEnum.code, baseExceptionEnum.msg, null);
    }

    public static <T> R<T> failed(BaseException.BaseExceptionEnum baseExceptionEnum, T data) {
        return new R<>(baseExceptionEnum.code, baseExceptionEnum.msg, data);
    }

    /**
     * 成功 true 成功 “异常” 对象信息
     *
     * @param baseExceptionEnum 成功 “异常” 对象信息
     */

    public static <T> R<T> success(BaseException.BaseExceptionEnum baseExceptionEnum, T data) {
        return new R<>(baseExceptionEnum.code, baseExceptionEnum.msg, data);
    }

    public static <T> R<T> success(BaseException.BaseExceptionEnum baseExceptionEnum) {
        return new R<>(baseExceptionEnum.code, baseExceptionEnum.msg, null);
    }

    public static <T> R<T> success(T data) {
        return new R<>(BaseException.BaseExceptionEnum.Success.code, BaseException.BaseExceptionEnum.Success.msg, data);
    }

    public static <T> R<T> success() {
        return new R<>(BaseException.BaseExceptionEnum.Success.code, BaseException.BaseExceptionEnum.Success.msg, null);
    }


    private R() {
    }

    private R(String code, String info, T data) {
        this.info = info;
        this.data = data;
        this.code = code;
        this.success = code.equals(BaseException.BaseExceptionEnum.Success.code);
    }
}