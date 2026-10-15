package com.aicode.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 关闭程序事件监听
 */
@Slf4j
@Component
public class ApplicationClosedEventListener {

    @Bean
    public GracefulShutdown gracefulShutdown() {
        return new GracefulShutdown();
    }

    private static class GracefulShutdown implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {
        private volatile Connector connector;

        @Override
        public void onApplicationEvent(ContextClosedEvent event) {
            log.warn("\n\n！！！---服务开始关闭---！！！\n\n");

            // connector 仅在 TomcatConnectorCustomizer#customize 中赋值。
            // 在 @SpringBootTest(MOCK) 等无嵌入式容器的场景下该回调不触发，
            // 此处若不判空会抛 NPE 并污染 context close 阶段的日志。
            // 判空后直接返回：此时本就没有连接器可关闭，不属于异常路径。
            if (this.connector == null) {
                log.debug("无嵌入式 Tomcat 连接器，跳过优雅停机处理（可能处于 MOCK 测试环境）");
                return;
            }

            this.connector.pause();
            Executor executor = this.connector.getProtocolHandler().getExecutor();
            if (executor instanceof ThreadPoolExecutor) {
                try {
                    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
                    threadPoolExecutor.shutdown();
                    if (!threadPoolExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                        log.warn("Tomcat thread pool did not shut down gracefully within 10 seconds. Proceeding with forceful shutdown");
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }

        }


        @Override
        public void customize(Connector connector) {
            this.connector = connector;
        }
    }
}
