package io.aicode.base.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@Slf4j
@ServerEndpoint(value = "/ws/{token}", configurator = WebsocketSpringCofigurator.class)
@Scope("prototype")
@Component
public class WebSocketServer {
    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        //加入缓存中
        WSClientManager.put(token, session);
        WSClientManager.sendMessage(token, "SOCKET_CONNECT_SUCCESS");
    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("token") String token) {
        WSClientManager.remove(token);
        WSClientManager.sendMessage(token, "SOCKET_CONNECT_EXIT");
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(@PathParam("token") String token, String message, Session session) {
        log.info("收到来自窗口" + token + "的信息:" + message);
        if (StringUtils.isEmpty(message)) {
            log.error("websocket信息不存在");
            return;
        }
        JSONObject cmdJson = JSON.parseObject(message);
        String name = cmdJson.getString("name");

        if (StringUtils.isEmpty(name)) {
            log.error("信息格式不正确");
            return;
        }
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
