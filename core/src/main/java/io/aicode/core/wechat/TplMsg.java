/*
 *
 *                        http://www.aicode.io
 *
 *
 *        本代码仅用于AI-Code.
 *
 */

package io.aicode.core.wechat;

import lombok.Data;

/**
 * @author lixin on 2016/9/18 0018.
 */
@Data
public class TplMsg {
    private String value;//数据
    private String color = "#173177";//字体颜色

    public TplMsg() {
    }

    public TplMsg(String value) {
        this.value = value;
    }

    public TplMsg(String value, String color) {
        this.value = value;
        this.color = color;
    }
}
