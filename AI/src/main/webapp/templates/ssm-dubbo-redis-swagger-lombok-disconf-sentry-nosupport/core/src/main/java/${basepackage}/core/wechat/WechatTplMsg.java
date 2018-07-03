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
 * 模板消息数据封装对象
 * {{first.DATA}}
 * 账单月份: {{keyword1.DATA}}
 * 账单金额: {{keyword2.DATA}}
 * 最低还款额:{{keyword3.DATA}}
 * 最后还款日:{{keyword4.DATA}}
 * {{remark.DATA}}
 *
 * @author lixin on 2016/9/14 0014.
 */
@Data
public class WechatTplMsg {
    private TplMsg first;
    private TplMsg keyword1;
    private TplMsg keyword2;
    private TplMsg keyword3;
    private TplMsg keyword4;
    private TplMsg userName;
    private TplMsg courseName;
    private TplMsg date;
    private TplMsg remark;

    public WechatTplMsg() {
    }

    public WechatTplMsg(TplMsg first, TplMsg keyword1, TplMsg keyword2, TplMsg remark) {
        this.first = first;
        this.keyword1 = keyword1;
        this.keyword2 = keyword2;
        this.remark = remark;
    }

    public WechatTplMsg(TplMsg first, TplMsg keyword1, TplMsg keyword2, TplMsg keyword3, TplMsg remark) {
        this.first = first;
        this.keyword1 = keyword1;
        this.keyword2 = keyword2;
        this.keyword3 = keyword3;
        this.remark = remark;
    }

    public WechatTplMsg(TplMsg first, TplMsg keyword1, TplMsg keyword2, TplMsg keyword3, TplMsg keyword4, TplMsg remark) {
        this.first = first;
        this.keyword1 = keyword1;
        this.keyword2 = keyword2;
        this.keyword3 = keyword3;
        this.keyword4 = keyword4;
        this.remark = remark;
    }
}
