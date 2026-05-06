package com.aicode.config;

import com.aicode.core.http.HttpHeaders;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;
import java.util.UUID;

public class MdcTaskDecorator implements TaskDecorator {


    /**
     * 处理异步线程打印traceId的问题
     *
     * @param runnable 异步
     * @return 返回的
     */
    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> map = MDC.getCopyOfContextMap();
        return () -> {
            try {
                if (null != map) {
                    MDC.setContextMap(map);
                } else {
                    MDC.put(HttpHeaders.TRACE_ID, UUID.randomUUID().toString());
                }

                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }
}