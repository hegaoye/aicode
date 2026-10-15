/*
* demo
 */
package com.aicode.exceptions;

import com.alibaba.fastjson2.JSON;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 数据库元数据探测 / 建库建表异常。
 *
 * <p>用于替代原先「catch 后只打一行 e.getMessage() 再静默返回」的做法 ——
 * 那使得「用户 SQL 有误」「schema 已存在但为空」「元数据查询条件失效」
 * 等成因全部收敛到同一个 9006，无法排障。</p>
 *
 * <p><b>⚠️ message 必须是合法 JSON</b>：{@code ExceptionHandle} 会把
 * {@code exception.getLocalizedMessage()} 直接交给 {@code JSON.parseObject(json)}，
 * 解析失败会导致异常处理器自身抛 {@code JSONException} 而失效（已实测）。
 * 因此自定义 message 一律通过 {@link #jsonMessage(String, Object...)} 构造，
 * 由 fastjson2 负责转义 schema 名中的引号等特殊字符。</p>
 *
 * @see com.aicode.database.dao.DatabaseDAO
 */
public class DatabaseException extends com.aicode.core.BaseException implements Serializable {

    public DatabaseException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage) {
        super(exceptionMessage);
    }

    public DatabaseException(com.aicode.core.BaseException.BaseExceptionEnum exceptionMessage, Object... params) {
        super(exceptionMessage, params);
    }

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造一个携带自定义信息的异常：message 为合法 JSON，原始异常作为 cause 保留。
     *
     * @param code    业务错误码
     * @param message 提示信息（可含 schema 名等上下文）
     * @param cause   原始异常，SHALL 保留以便定位 SQL 失败位置
     */
    public static DatabaseException of(String code, String message, Throwable cause) {
        return new DatabaseException(jsonMessage(code, message), cause);
    }

    /** 构造合法 JSON 的 message，避免 ExceptionHandle 的 parseObject 失效。 */
    static String jsonMessage(String code, String message) {
        Map<String, String> m = new LinkedHashMap<>(2);
        m.put("code", code);
        m.put("msg", message);
        return JSON.toJSONString(m);
    }
}
