/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于AI-Code.
 *
 */

package ${basePackage}.core.wechat;

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
