/*
 *
 *                        http://www.aicode.io
 *
 *
 *        本代码仅用于AI-Code.
 *
 */

package io.aicode.base.wechat;

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






