package ${basePackage}.core.tools;

import com.alibaba.fastjson.JSON;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
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
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long t1 = System.nanoTime();
        Response response = chain.proceed(chain.request());
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
                .body(ResponseBody.create(mediaType, content))
                .build();
    }
}