/*
 * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于AI-Code.目.
 */
package ${basePackage}.core.tools;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


/**
 * HTTP工具类，获取用户真实IP等信息
 * @author CCW
 * 2017-03-31
 */

public class HttpUtil {
	private final static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	
	/**
	 * 获取用户真实IP
	 *
	 * 2017-03-31
	 * @param request
	 * @return IP
	 */
	public String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 *
	 * @param url   发送请求的 URL
	 * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param) {
		List formparams = params2Map(param);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		String result = "";         //post结果
		RequestConfig config = RequestConfig.custom()
				.setConnectionRequestTimeout(10000)     //设置连接请求超时
				.setConnectTimeout(10000)               //设置连接超时
				.setSocketTimeout(10000)                //设置请求超时
				.build();
		CloseableHttpResponse response = null;
		try {
			post.setEntity(new UrlEncodedFormEntity(formparams, "UTF-8"));
			post.setConfig(config);
			response = httpClient.execute(post);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "UTF-8");
			EntityUtils.consume(entity);
		} catch (ClientProtocolException e) {
			logger.error("post 请求异常 catch：ClientProtocolException = " + e);
		} catch (IOException e) {
			logger.error("post 请求异常 catch：IOException = " + e);
		} finally {
			closeResponse(response);
		}
		return result;
	}

	/**
	 * 向指定 URL 发送 GET 方法的请求
	 * @param url 发送请求的 URL
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendGet(String url) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		String result = "";         //post结果
		RequestConfig config = RequestConfig.custom()   //自定义请求参数
				.setConnectionRequestTimeout(10000)     //设置连接请求超时
				.setConnectTimeout(10000)               //设置连接超时
				.setSocketTimeout(10000)                //设置请求超时
				.build();
		CloseableHttpResponse response = null;
		try {
			get.setConfig(config);
			response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "UTF-8").trim();
			EntityUtils.consume(entity);
		} catch (ClientProtocolException e) {
			logger.error("get 请求异常 catch：ClientProtocolException = " + e);
		} catch (IOException e) {
			logger.error("get 请求异常 catch：IOException = " + e);
		} finally {
			closeResponse(response);
		}
		return result;
	}
	
	
	/**
	 * POST请求
	 *
	 * 2017-03-31
	 * @param url 请求路径
	 * @param param 请求参数
	 * @return json格式字符串
	 */
	public static String doPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		 return result;
	}

	/**
	 * &符号隔开的请求参数字符串，转Map
	 *
	 * @param param
	 * @return
	 */
	private static List params2Map(String param) {
		List formparams = new ArrayList();
		String[] paramAry = param.split("&");
		for (int i = 0; i < paramAry.length; i++) {
			String str = paramAry[i];
			String key = str.substring(0, str.indexOf("="));
			String value = str.substring(str.indexOf("=") + 1, str.length());
			formparams.add(new BasicNameValuePair(key, value));
		}
		return formparams;
	}

	/**
	 * 关闭 http响应实例
	 * @param response
	 */
	public static void closeResponse(CloseableHttpResponse response) {
		try {
			if (response != null) {
				response.close();
			}
		} catch (IOException e) {
			logger.error("关闭 http响应 finally catch：IOException = " + e);
		}
	}

	/*public static String okget(String url) {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url(uri)
				.get()
				.addHeader("accept", "application/json")
				.addHeader("content-type", "application/json")
				.build();

		Response response = null;
		response = client.newCall(request).execute();
		if (!response.isSuccessful()) {
			log.error("【实名认证失败】-由于" + response.message());
			return RetMsg.create(false, response.message());
		}
		// 2.解析数据
		String jsonResult = response.body().string();
	}*/
}
