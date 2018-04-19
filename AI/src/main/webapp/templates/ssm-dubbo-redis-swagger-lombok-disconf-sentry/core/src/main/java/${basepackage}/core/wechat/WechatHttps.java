/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于AI-Code.
 *
 */

package ${basePackage}.core.wechat;

import com.alibaba.fastjson.JSON;
import ${basePackage}.core.Env;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * 发送微信https请求
 *
 * @author lixin on 2016/9/12 0012.
 */
public class WechatHttps {
    private final static Logger logger = LoggerFactory.getLogger(WechatHttps.class);

    public static String ORIGIN_ID = null;//公众号原始ID
    public static String APP_ID = null;//微信公众账号AppID(应用ID)
    public static String APP_SECRECT = null;//微信公众账号AppSecret(应用密钥)
    public static String API_TOKEN = null;//微信通知接口TOKEN
    //微信通知Token接口
    public static String TOKEN_URI = null;

    //发送模板消息接口
    public static String MSG_TPL_URI = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=#access_token#";

    //微信认证获取用户信息接口 openid是公众号的普通用户的一个唯一的标识，只针对当前的公众号有效
    public static String USERINFO_URI = "https://api.weixin.qq.com/sns/userinfo?access_token=#token#&openid=#openid#&lang=zh_CN";

    //微信刷新token接口
    public static String REFRESH_TOKEN_URI = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=#appid#&grant_type=refresh_token&refresh_token=#token#";

    //微信认证的接口，获取token
    public static String ACCESS_TOKEN_URI = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=#appid#&secret=#secret#&code=#code#&grant_type=authorization_code";

    //微信认证的url接口
    public static String OAUTH2_URI = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=#appid#&redirect_uri=#uri#&response_type=code&scope=snsapi_base&state=100#wechat_redirect";

    static {
        switch (Env.env) {
            case DEVELOP:
                logger.info("----------开启开发环境-------------");
                //微信测试公众平台
                ORIGIN_ID = "gh_6c027d862a13";
                APP_ID = "wx91a45ef85e74f2e0";
                APP_SECRECT = "9d4d5cdcb3e0a284d069ffcc69a71d54";
                break;
            case SANDBOX:
                logger.info("----------开启测试环境-------------");
                ORIGIN_ID = "gh_53ddc26cf500";
                APP_ID = "wxfd3609639375a609";
                APP_SECRECT = "57410db8cbe17a16a7b976187c66fcb1";
                break;
            case PRODUCT:
                logger.info("----------开启生产环境-------------");
                ORIGIN_ID = "gh_53ddc26cf500";
                APP_ID = "wxfd3609639375a609";
                APP_SECRECT = "eae3c65a1c4a1c6e16f1a68a65a66f5d";
                break;
        }
    }

    /**
     * 发送https GET请求
     *
     * @param url  请求地址
     * @param data 提交的数据
     * @return json 字符串
     */
    public static String httpsGet(String url, String data) {
        return httpsRequest(url, "GET", data);
    }


    /**
     * 发送https POST请求
     *
     * @param url  请求地址
     * @param data 提交的数据
     * @return json 字符串
     */
    public static String httpsPost(String url, String data) {
        return httpsRequest(url, "POST", data);
    }


    /**
     * 发送https POST请求
     *
     * @param wxid         微信id
     * @param tplno        模板id（微信提供）
     * @param linkurl      微信消息跳转地址
     * @param wechatTplMsg 数据信息
     * @return json 字符串
     */
    public static String httpsPost(String wxid, String tplno, String linkurl, WechatTplMsg wechatTplMsg, String token) {
        WechatTplRequest wechatTplRequest = new WechatTplRequest(wxid, tplno, linkurl, wechatTplMsg);
        String data = JSON.toJSONString(wechatTplRequest);
        String uri = WechatHttps.MSG_TPL_URI.replace("#access_token#", token);
        logger.info("====> [String httpsPost(String wxid, String tplno, String linkurl, WechatTplMsg wechatTplMsg, String token)]");
        logger.info("====> " + uri);
        logger.info("====> " + ToStringBuilder.reflectionToString(data, ToStringStyle.MULTI_LINE_STYLE));
        return httpsPost(uri, data);
    }

    /**
     * 发送https POST请求
     *
     * @param url          请求地址
     * @param wxid         微信id
     * @param tplno        模板id（微信提供）
     * @param linkUrl      微信消息跳转地址
     * @param wechatTplMsg 数据信息
     * @return json 字符串
     */
    public static String httpsPost(String url, String wxid, String tplno, String linkUrl, WechatTplMsg wechatTplMsg) {
        WechatTplRequest wechatTplRequest = new WechatTplRequest(wxid, tplno, linkUrl, wechatTplMsg);
        String data = JSON.toJSONString(wechatTplRequest);
        return httpsPost(url, data);
    }

    /**
     * 二：通过code换取网页授权access_token
     *
     * @return {
     * "access_token":"ACCESS_TOKEN",
     * "expires_in":7200
     * }
     */
    public static Map<String, Object> getAccessToken() {
        String appid = WechatHttps.APP_ID;
        String secret = WechatHttps.APP_SECRECT;
        String access_token_uri = WechatHttps.TOKEN_URI.replace("#appid#", appid).replace("#secret#", secret);
        String json = WechatHttps.httpsGet(access_token_uri, null);
        return json == null ? null : JSON.parseObject(json, Map.class);
    }

    /**
     * 微信模板消息发送https请求
     *
     * @param requestUrl    请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param requestData   提交的数据
     * @return json 字符串
     */
    private static String httpsRequest(String requestUrl, String requestMethod, String requestData) {
        logger.info("====> 微信模板消息发送https请求 [String httpsRequest(String requestUrl, String requestMethod, String requestData)]");
        logger.info("====> requestUrl::" + requestUrl);
        logger.info("====> requestMethod::" + requestMethod);
        logger.info("====> requestData::" + requestData);

        String json = null;
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = {new WechatX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(ssf);

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);

            // 当outputStr不为null时向输出流写数据
            if (null != requestData) {
                OutputStream outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(requestData.getBytes("UTF-8"));
                outputStream.close();
            }

            // 从输入流读取返回内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }

            json = buffer.toString();
            logger.info("<==== " + json);
            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            conn.disconnect();
        } catch (ConnectException ce) {
            logger.error("微信模板消息发送https请求连接超时：{}", ce);
        } catch (Exception e) {
            logger.error("微信模板消息发送https请求异常：{}", e);
        }
        return json;
    }


}

/**
 * 信任管理器
 */
class WechatX509TrustManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
