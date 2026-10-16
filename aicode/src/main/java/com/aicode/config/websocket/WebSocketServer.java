package com.aicode.config.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * WebSocket 端点 —— 会话按 URL 的 {@code buildId} 参数分房间登记。
 *
 * <p><b>2026-10-18 改造</b>（{@code openspec/changes/2026-10-18-websocket-room-routing}）：
 * 前端两个 ws hook（{@code useBuildLog} / {@code useInitProgress}）的 URL 一直带
 * {@code ?buildId=<projectCode>}，但本类此前完全忽略它、一律塞进单一房间 ——
 * 结果是只保留第一个连接，第二个标签页收不到任何推送。</p>
 *
 * <p>{@code @OnClose} 现接收 {@link Session} 参数：多房间下必须知道关的是哪个房间的
 * 连接，否则无法正确注销、连接泄漏。</p>
 */
@Slf4j
@ServerEndpoint(value = "/websocket.shtml", configurator = WebsocketSpringCofigurator.class)
@Scope("prototype")
@Component
public class WebSocketServer {

    /**
     * 连接建立成功。
     *
     * <p>房间取自 URL 的 {@code buildId} 查询参数（缺省时由
     * {@link WSClientManager#DEFAULT_ROOM} 兜底），使消息能按项目精确投递。</p>
     */
    @OnOpen
    public void onOpen(Session session) {
        WSClientManager.put(session, roomOf(session));
    }

    /**
     * 连接关闭。
     *
     * <p><b>必须带 {@link Session}</b>：房间是多值的，缺了它就不知道该注销哪个连接。</p>
     */
    @OnClose
    public void onClose(Session session) {
        WSClientManager.remove(session);
    }

    /**
     * 收到客户端消息后调用。
     *
     * @param message 客户端发送过来的消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来自窗口({})的信息:{}", roomOf(session), message);
    }

    /**
     * 连接错误。
     *
     * <p>一并清理该会话：异常断开时 {@code @OnClose} 可能不触发，
     * 残留会话会在后续投递时被 {@code isOpen()} 检查顺带移除，但此处提前清理更及时。</p>
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket 处理异常, room:{}", roomOf(session), error);
        WSClientManager.remove(session);
    }

    /**
     * 从连接的 query 参数里取房间标识。
     *
     * <p>{@code @OnOpen} 时 Spring 的容器已经把 query 参数放进
     * {@code session.getRequestParameterMap()}，故无需解析 URL 字符串。</p>
     *
     * @return 房间标识；缺失或非单值时返回 {@code null}（由 manager 兜底为默认房间）
     */
    private String roomOf(Session session) {
        String room = null;
        try {
            var params = session.getRequestParameterMap();
            if (params != null && params.get("buildId") != null
                    && params.get("buildId").size() == 1) {
                room = params.get("buildId").get(0);
            }
        } catch (Exception e) {
            log.warn("读取 buildId 参数失败，回退默认房间", e);
        }
        return room;
    }
}