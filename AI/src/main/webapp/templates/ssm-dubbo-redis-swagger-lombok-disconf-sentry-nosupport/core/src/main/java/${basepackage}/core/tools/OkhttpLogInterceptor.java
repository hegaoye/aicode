/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于AI-Code.
 *
 */

package ${basePackage}.core.tools;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by lixin on 2017/11/6.
 */
public class OkhttpLogInterceptor implements Interceptor {
    private final static Logger logger = LoggerFactory.getLogger(OkhttpLogInterceptor.class);

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long t1 = System.nanoTime();
        okhttp3.Response response = chain.proceed(chain.request());
        long t2 = System.nanoTime();
        ResponseBody responseBody = response.peekBody(1024 * 1024);
        logger.info(String.format("发送请求 %s on %s%n%s",
                request.url(), request.method(), chain.connection(), request.headers()));
        logger.info(String.format("接收响应: [%s] %n返回 JSON : %n%s %n耗时 ： %.1fms%n%s",
                response.request().url(),
                JSON.toJSONString(responseBody.string()),
                (t2 - t1) / 1e6d,
                response.headers()));
        okhttp3.MediaType mediaType = response.body().contentType();
        String content = response.body().string();
        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, content))
                .build();
    }
}