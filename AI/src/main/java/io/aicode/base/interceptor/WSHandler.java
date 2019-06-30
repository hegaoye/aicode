package io.aicode.base.interceptor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
//import io.aicode.base.tools.TerminalEmulator;
import io.aicode.base.tools.SSH2;
import io.aicode.base.tools.SSHResInfo;
import io.aicode.project.ctrl.SSHClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;

import java.io.*;
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
//    private ConcurrentMap<String, TerminalEmulator> connectors = new ConcurrentHashMap<>();
    private Multimap<String, String> sessions = HashMultimap.create();

    private SSH2 ssh2 = null;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug("connect to the websocket success......");
        session.sendMessage(new TextMessage("OK"));
        wsClientManager.put(session);
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


//        scan = new Scanner(cmd);
//        while (scan.hasNextLine()) {
//            wss.sendMessage(new TextMessage(scan.nextLine()));
//        }


//        this.test(wss, cmd);
//        if (sshClient == null) {
//            sshClient = new SSHClient("192.168.1.220", "pitop", "0");
//        }
//        String result = sshClient.execute(cmd);
//        wss.sendMessage(new TextMessage(result));


        log.info(cmd);
//        wss.sendMessage(new TextMessage(cmd));
//        test(wss, cmd);
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
                    e.printStackTrace();
                    helper.close();
                }
            } catch (JSchException e) {
                e.printStackTrace();
            }
        }
    }

    Session session = null;
    Channel channel = null;
    Scanner scanner = null;
    PrintWriter sshout;  // SSH 輸出端

    public void test(WebSocketSession webSocketSession, String cmd) throws JSchException, IOException {
        if (session == null) {
            JSch jsch = new JSch();
            session = jsch.getSession("pitop", "192.168.1.220", 22);
            session.setPassword("0");
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(50000);

            channel = session.openChannel("shell");
            PipedInputStream pipedInputStream;
            PipedOutputStream pipedOutputStream;
            pipedInputStream = new PipedInputStream();
            pipedOutputStream = new PipedOutputStream();
            pipedInputStream.connect(pipedOutputStream);
            channel.setInputStream(pipedInputStream);
            sshout = new PrintWriter(pipedOutputStream, true);
//        channel.setInputStream(new ByteArrayInputStream((cmd + "\n").getBytes()));

            // 创建输出通道
            pipedInputStream = new PipedInputStream();
            pipedOutputStream = new PipedOutputStream();
            pipedInputStream.connect(pipedOutputStream);
            channel.setOutputStream(pipedOutputStream);
            scan = new Scanner(pipedInputStream, "UTF-8");
            channel.connect(3 * 1000);
        }

//        InputStream inputStream1 = channel.getInputStream();
//        byte[] tmp1 = new byte[1024];
        sshout.println(cmd);
        sshout.flush();
        while (scan.hasNextLine()) {
            webSocketSession.sendMessage(new TextMessage(scan.nextLine()));
        }


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
