package com.rzhkj.base.tools;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * Created by lixin on 2018/10/12.
 */
public class WSTools {
    private WebSocketSession webSocketSession;

    public WSTools(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    public void send(String msg) {
        try {
            this.webSocketSession.sendMessage(new TextMessage(msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
