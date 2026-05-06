package com.aicode.core.http;

/**
 * 自定义header 头
 */
public interface HttpHeaders {
    /**
     * 日志追踪id
     */
    String TRACE_ID = "traceId";
    /**
     * 请求id
     */
    String REQUEST_ID = "Request-Id";
    /**
     * 客户端ip
     */
    String CLIENT_IP = "Client-Ip";
    /**
     * 请求 token header 头
     */
    String AUTHORIZATION = "Authorization";
    /**
     * 谷歌id
     */
    String GOOGLE_ID = "Request-Google-Id";
    /**
     * 路由主机
     */
    String GATEWAY_HOST = "X-Auth-Gateway-Host";
    /**
     * gateway 请求 id
     */
    String GATEWAY_REQUEST_ID = "X-Gateway-Request-Id";
    /**
     * 用户id
     */
    String USER_ID = "User-Id";
    /**
     * 用户名
     */
    String USER_NAME = "User-Name";
    /**
     * 用户浏览器时区
     */
    String TIME_ZONE = "Time-Zone";
    /**
     * Nonce
     */
    String NONCE = "Nonce";
    /**
     * Timestamp
     */
    String TIMESTAMP = "Timestamp";
    /**
     * 设备id
     */
    String DEVICE_ID = "Device-Id";
    /**
     * 客户端类型
     */
    String CLIENT_TYPE = "Client-Type";
    /**
     * 请求主机名
     */
    String REQUEST_HOST = "Request-Host";
}
