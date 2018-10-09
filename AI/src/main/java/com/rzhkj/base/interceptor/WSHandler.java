package com.rzhkj.base.interceptor;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.rzhkj.core.tools.SSH2;
import com.rzhkj.core.tools.SSHResInfo;
import com.rzhkj.project.ctrl.SSHClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by lixin on 2018/9/30.
 */
@Slf4j
public class WSHandler implements WebSocketHandler {
    @Autowired
    private WSClientManager wsClientManager;
    public static Map<String, Object> map = new HashedMap();

    private SSH2 ssh2 = null;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug("connect to the websocket success......");
        session.sendMessage(new TextMessage("Server:connected OK!"));
        wsClientManager.setWSS.add(session);
    }

    SSHClient sshClient = null;

    @Override
    public void handleMessage(WebSocketSession wss, WebSocketMessage<?> wsm) throws Exception {
        String cmd = wsm.getPayload().toString();
        log.debug(wss.getHandshakeHeaders().getFirst("Cookie"));
//        if (ssh2 == null) {
//            ssh2 = new SSH2("192.168.1.220", 22, "pitop", "0");
//        }
//        SSHResInfo resInfo = ssh2.sendCmd(cmd);
//        TextMessage returnMessage = new TextMessage(resInfo.toString());
//        wss.sendMessage(returnMessage);

//        this.test(wss, cmd);
        if (sshClient == null) {
            sshClient = new SSHClient("192.168.1.220", "pitop", "0");
        }
        String result = sshClient.execute(cmd);
        wss.sendMessage(new TextMessage(result));
//        test2(wss, cmd);
    }


    SSH2 helper = null;
    Scanner scan = null;

    public void test2(WebSocketSession webSocketSession, String cmd) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(cmd.getBytes());
        if (scan == null) {
            scan = new Scanner(inputStream, "UTF-8");
        }
        String read = scan.nextLine();
        if (read != null) {
            try {
                if (helper == null) {
                    //使用目标服务器机上的用户名和密码登陆
                    helper = new SSH2("192.168.1.220", 22, "pitop", "0");
                }
                try {
                    SSHResInfo resInfo = helper.shell(read, 200);
                    System.out.println(resInfo.toString());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    helper.close();
                }
            } catch (JSchException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    Session session = null;
    Channel channel = null;

    public void test(WebSocketSession webSocketSession, String cmd) throws JSchException, IOException {
        if (session == null) {
            JSch jsch = new JSch();
            session = jsch.getSession("pitop", "192.168.1.220", 22);
            session.setPassword("0");
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(50000);
        }

        channel = session.openChannel("shell");
        channel.setInputStream(new ByteArrayInputStream((cmd + "\n").getBytes()));

        InputStream inputStream1 = channel.getInputStream();
        byte[] tmp1 = new byte[1024];

        channel.connect(3 * 1000);

        new Thread() {
            public void run() {
                try {
                    while (true) {
                        StringBuffer stringBuffer = new StringBuffer();
                        while (inputStream1.available() > 0) {
                            int i = inputStream1.read(tmp1, 0, 1024);
                            stringBuffer.append(new String(tmp1, 0, i));
                        }
                        if (stringBuffer.length() <= 0) {
                            return;
                        }
                        try {
                            Thread.sleep(150);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (stringBuffer.length() > 0) {
                            String result = new String(stringBuffer.toString().getBytes("ISO-8859-1"), "UTF-8");
                            log.debug(result);
                            webSocketSession.sendMessage(new TextMessage(result));
//                            return;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    @Override
    public void handleTransportError(WebSocketSession wss, Throwable thrwbl) throws Exception {
        if (wss.isOpen()) {
            wss.close();
            wsClientManager.setWSS.remove(wss);
        }
        log.debug("websocket connection closed......");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession wss, CloseStatus cs) throws Exception {
        log.debug("websocket connection closed......");
        wsClientManager.setWSS.remove(wss);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
