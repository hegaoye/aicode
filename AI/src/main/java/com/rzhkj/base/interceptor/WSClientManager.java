package com.rzhkj.base.interceptor;

import java.util.HashSet;
import java.util.Set;

import org.springframework.web.socket.WebSocketSession;

/**
 * Created by lixin on 2018/9/30.
 */
public class WSClientManager {
    //这里只是举个例子，所以不写线程保护代码了。
    public Set<WebSocketSession> setWSS = new HashSet<WebSocketSession>();
}
