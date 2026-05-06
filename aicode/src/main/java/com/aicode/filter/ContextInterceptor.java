package com.aicode.filter;

import com.baidu.fsg.uid.UidGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class ContextInterceptor implements HandlerInterceptor {
    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        log.info("接口请求,uri: {}", uri);
        //日志追踪 id
        String traceId = request.getHeader("traceId");
        log.info("日志追踪 traceId: {}", traceId);

        if (StringUtils.isBlank(traceId)) {
            traceId = String.valueOf(uidGenerator.getUID());
        }
        MDC.put("traceId", traceId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}