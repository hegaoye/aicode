package com.rzhkj.base.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lixin on 2018/9/30.
 */
@Component
public class WSClientManager {
    //这里只是举个例子，所以不写线程保护代码了。
    private Map<String, WebSocketSession> sessionMap = new HashMap<>();

    public void put(WebSocketSession webSocketSession) {
        String address = webSocketSession.getRemoteAddress().getHostString().replace(".", "_");
        sessionMap.put(address, webSocketSession);
    }

    public void remove(WebSocketSession webSocketSession) {
        String address = webSocketSession.getRemoteAddress().getHostString().replace(".", "_");
        sessionMap.remove(address);
    }

    public WebSocketSession get(String address) {
        address = address.replace(".", "_");
        if (sessionMap.containsKey(address)) {
            return sessionMap.get(address);
        }
        return null;
    }

}
