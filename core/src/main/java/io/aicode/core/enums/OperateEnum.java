/*
 *
 *                       http://www.aicode.io
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
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
