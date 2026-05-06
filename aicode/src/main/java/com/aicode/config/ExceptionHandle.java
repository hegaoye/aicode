package com.aicode.config;

import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常拦截器
 * 处理异常从底层抛出后，封装为统一格式返回给请求
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandle {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<R> catchException(Exception exception) {
        String errorMsg = null;
        JSONObject jsonObject = null;
        if (exception != null && exception instanceof BaseException) {
            String json = exception.getLocalizedMessage();
            jsonObject = JSON.parseObject(json);
        } else {
            errorMsg = BaseException.BaseExceptionEnum.Server_Error.toString();
            log.error("{}", errorMsg);
            jsonObject = JSON.parseObject(errorMsg);
        }

        if (null == jsonObject) {
            errorMsg = BaseException.BaseExceptionEnum.Server_Error.toString();
            jsonObject = JSON.parseObject(errorMsg);
        }

        log.error("{}", exception.getLocalizedMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonObject.toJavaObject(R.class));
    }


}