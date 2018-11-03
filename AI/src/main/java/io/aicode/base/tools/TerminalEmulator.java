/*
 * Copyright (c) 2014-2016 CODING.
 */

package io.aicode.base.tools;

import com.pty4j.PtyProcess;
import lombok.extern.log4j.Log4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by tan on 16/8/11.
 */
@Log4j
public class TerminalEmulator extends Thread {

    Charset charset = Charset.forName("UTF-8");

    private TtyConnector ttyConnector;

    private WebSocketSession wss;

    private String termId;


    public TerminalEmulator(String termId, WebSocketSession wss, String[] command, Map<String, String> envs, String workingDirectory) throws IOException {
        PtyProcess pty = PtyProcess.exec(command, envs, workingDirectory);// working dir
        this.wss = wss;
        this.termId = termId;
        this.ttyConnector = new ProcessTtyConnector(pty, charset);
    }

    public void run() {
        while (!Thread.interrupted() && ttyConnector.isConnected()) {
            try {
                String s = ttyConnector.read();
                log.debug(s);
                wss.sendMessage(new TextMessage(s));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ttyConnector.close();

        log.info("pty exit success");
    }

    public void write(String msg) throws IOException {
        ttyConnector.write(msg);
    }

    public void resize(Integer cols, Integer rows) {
        ttyConnector.resize(cols, rows);
    }

    public void close() {
        try {
            interrupt();
            log.debug("shell.exit");
            wss.sendMessage(new TextMessage("shell.exit"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
