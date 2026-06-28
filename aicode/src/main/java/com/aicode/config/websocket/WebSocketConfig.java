package com.aicode.config.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket 端点导出器。
 *
 * <p>阶段 1 范围：保留 Jakarta WebSocket 默认同步回调语义。
 * {@code @OnMessage} / {@code @OnOpen} 都在 Tomcat WebSocket 线程池执行，
 * 不是 virtual thread。</p>
 *
 * <p>阶段 2 计划（独立 PR）：在 {@code WSClientManager.sendMessage} 内部
 * 投递到独立 VT 池，避免阻塞 Tomcat 推送线程。</p>
 */
@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean
    public WebsocketSpringCofigurator websocketSpringCofigurator() {
        return new WebsocketSpringCofigurator(); // This is just to get context
    }
}

