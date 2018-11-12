/*
 *
 *                       http://www.aicode.io
 *
 *
 *      本代码仅用于AI-Code.
 */

package io.aicode.core.enums;

/**
 * 操作名称定义，适用于添加日志操作详情说明
 * Created by lixin on 2017/6/7.
 */
public enum OperateEnum {
    INSERT("添加操作"), UPDATE("修改操作"), DELETE("删除操作");
    public String operate;

    OperateEnum(String operate) {
        this.operate = operate;
    }
}
