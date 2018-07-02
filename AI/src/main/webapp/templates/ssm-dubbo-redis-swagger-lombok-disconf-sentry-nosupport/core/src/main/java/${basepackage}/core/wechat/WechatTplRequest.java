/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于AI-Code.
 *
 */

package ${basePackage}.core.wechat;

/**
 * 微信模板消息请求数据对象
 * <p>
 * 模板路径:https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN
 * 数据格式：
 * {
 * "touser":"OPENID",
 * "template_id":"ngqIpbwh8bUfcSsECmogfXcV14J0tQlEpBO27izEYtY",
 * "url":"http://weixin.qq.com/download",
 * "data":{
 * "first": {
 * "value":"恭喜你购买成功！",
 * "color":"#173177"
 * },
 * "keynote1":{
 * "value":"巧克力",
 * "color":"#173177"
 * },
 * "keynote2": {
 * "value":"39.8元",
 * "color":"#173177"
 * },
 * "keynote3": {
 * "value":"2014年9月22日",
 * "color":"#173177"
 * },
 * "remark":{
 * "value":"欢迎再次购买！",
 * "color":"#173177"
 * }
 * }
 * }
 *
 * @author lixin
 */
public class WechatTplRequest {
    private String touser;
    private String template_id;
    private String url;
    private WechatTplMsg data;

    public WechatTplRequest() {
    }

    public WechatTplRequest(String touser, String template_id, String url, WechatTplMsg data) {
        this.touser = touser;
        this.template_id = template_id;
        this.url = url;
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public WechatTplMsg getData() {
        return data;
    }

    public void setData(WechatTplMsg data) {
        this.data = data;
    }
}






