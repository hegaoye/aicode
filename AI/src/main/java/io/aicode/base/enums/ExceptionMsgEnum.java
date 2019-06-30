/*
 *
 *                       http://www.aicode.io
 *
 *
 *      本代码仅用于AI-Code.
 */

package io.aicode.base.enums;


import com.alibaba.fastjson.JSON;

/**
 * 项目中异常处理枚举类
 * Created by lixin on 2017/6/3.
 */
public enum ExceptionMsgEnum {
    RESULT_IS_NULL("1001", "The query result does not exist!"),
    PARAM_IS_NULL("1002", "Parameter is empty!"),
    EXISTS("1003", "Information already exists ！"),
    UPDATE_FAILED("1004", "Update failed！"),
    UPLOAD_FAILED("1005", "Upload failed！"),
    State_ERROR("1006", "State error！"),
    PARAM_ERROR("1007", "Parameter is not legal！"),
    SERVER_ERROR("1999", "Server Error");

    private String errorCode;    //异常编号
    private String message;     //异常信息

    ExceptionMsgEnum(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }


    /**
     * 返回异常字符串
     *
     * @return
     */
    public String toString() {
        return JSON.toJSONString(this);
    }

    /**
     * 根据异常编号查询异常信息
     *
     * @param code 异常编号
     * @return
     */
    public static String getMessage(String code) {
        String result = RESULT_IS_NULL.getMessage();
        for (ExceptionMsgEnum enums : ExceptionMsgEnum.values()) {
            if (code.equals(enums.getErrorCode())) {
                result = enums.getMessage();
                break;
            }
        }
        return result;
    }

    /**
     * 根据异常信息查询异常编号
     *
     * @param msg 异常信息
     * @return
     */
    public static String getErrorCode(String msg) {
        String result = RESULT_IS_NULL.getErrorCode();
        for (ExceptionMsgEnum enums : ExceptionMsgEnum.values()) {
            if (msg.equals(enums.getMessage())) {
                result = enums.getErrorCode();
                break;
            }
        }
        return result;
    }
}
