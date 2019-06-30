package io.aicode.base.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;

import java.util.Map;

/**
 * Created by lixin on 2018/9/30.
 */
@Slf4j
public class WSHandler implements WebSocketHandler {
    @Autowired
    private WSClientManager wsClientManager;
    public static Map<String, Object> map = new HashedMap();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug("connect to the websocket success......");
        session.sendMessage(new TextMessage("OK"));
        wsClientManager.put(session);
    }



    @Override
    public void handleMessage(WebSocketSession wss, WebSocketMessage<?> wsm) throws Exception {
        String cmd = wsm.getPayload().toString();
        log.info(cmd);

    }

    @Override
    public void handleTransportError(WebSocketSession wss, Throwable thrwbl) throws Exception {
        if (wss.isOpen()) {
            wss.close();
            wsClientManager.remove(wss);
        }
        log.debug("websocket connection closed......");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession wss, CloseStatus cs) throws Exception {
        log.debug("websocket connection closed......");
        wsClientManager.remove(wss);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
