package io.aicode.base.tools;

import lombok.extern.java.Log;
import org.springframework.web.socket.TextMessage;

import javax.websocket.Session;
import java.io.IOException;

/**
 * Created by lixin on 2018/10/12.
 */
@Log
public class WSTools {
    private Session webSocketSession;

    public WSTools(Session webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

//    public void send(String msg) {
//        try {
//            if (this.webSocketSession.isOpen()) {
//                this.webSocketSession.send(new TextMessage("# " + msg));
//            } else {
//                log.warning("WebSocket 连接已经关闭");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public void send(String username, String host, String path, String msg) {
//        try {
//            if (this.webSocketSession.isOpen()) {
//                this.webSocketSession.sendMessage(new TextMessage(username + "@" + host + ":" + path + "# " + msg));
//            } else {
//                log.warning("WebSocket 连接已经关闭");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
