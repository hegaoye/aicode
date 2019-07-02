package io.aicode.base.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket链接管理器
 *
 * @author shangze
 */
@Component
@Slf4j
public class WSClientManager {
    //线程安全map
    private static Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    //静态变量，用来记录当前在线连接数。
    private static int onlineCount = 0;

    public static void put(String token, Session session) {
        Session ss = get(token);
        if (ss == null) {
            sessionMap.put(token, session);
            addOnlineCount();           //在线数加1
            log.info("有新窗口开始监听:" + token + ",当前在线人数为" + getOnlineCount());
        }
    }

    public static void remove(String token) {
        sessionMap.remove(token);
        subOnlineCount();           //在线数减1
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    public static Session get(String token) {
        if (sessionMap.containsKey(token)) {
            return sessionMap.get(token);
        }
        return null;
    }

    /**
     * 实现服务器主动推送
     */
    public static void sendMessage(String token, String message) {
        Session session = get(token);
        if (session != null) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendShellMsg(String token, String username, String host, String path, String msg) {
        sendMessage(token, username + "@" + host + ":" + path + "# " + msg);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WSClientManager.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WSClientManager.onlineCount--;
    }
}
