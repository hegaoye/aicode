package com.aicode.config;

import com.aicode.core.exceptions.BaseException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Created by hegaoye on 2018/8/13.
 */
@Configuration
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        log.debug("methodKey >>> " + methodKey);
        try {
            String message = Util.toString(response.body().asReader());
            return new BaseException(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
