package io.aicode.base.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@Slf4j
@ServerEndpoint(value = "/websocket.shtml", configurator = WebsocketSpringCofigurator.class)
@Scope("prototype")
@Component
public class WebSocketServer {
    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        //加入缓存中
        WSClientManager.put(session);
    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        WSClientManager.remove();
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来自窗口的信息:" + message);
    }


    /**
     * 连接错误调用的方法
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }
}
