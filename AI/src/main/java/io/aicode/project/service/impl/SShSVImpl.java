/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package io.aicode.project.service.impl;

import com.jcraft.jsch.*;
import io.aicode.base.tools.WSTools;
import io.aicode.project.entity.SSh;
import io.aicode.project.service.SShSV;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
@Service
public class SShSVImpl implements SShSV {
    private ConcurrentMap<String, Session> connectors = new ConcurrentHashMap<>();
    private Map<String, Channel> channels = new HashMap<>();

    /**
     * 关闭ssh shell连接
     * 1.关闭输出流
     * 2.关闭通讯管道
     *
     * @param printWriter 写出流
     * @param channel     通讯管道
     * @return
     */
    @Override
    public boolean close(PrintWriter printWriter, Channel channel) {
        String key = "test";
        if (channels.containsKey(key + "channels")) {
            channel = channels.get(key + "channels");
        }
        //1.关闭输出流
        if (printWriter != null) {
            printWriter.print("exit");
            printWriter.flush();
            printWriter.close();
        }

        //2.关闭通讯管道
        if (channel != null) {
            if (channel.isConnected()) {
                channel.disconnect();
            }
        }
        return true;
    }

    /**
     * @param codes    代码
     * @param fileName 文件名称
     * @param sSh      ssh连接对象信息
     */
    @Override
    public void sftpUpload(String codes, String fileName, String path, SSh sSh) throws JSchException, SftpException {
        /** 主机 */
        String host = sSh.getHost();
        /** 端口 */
        int port = sSh.getPort();
        /** 用户名 */
        String username = sSh.getUser();
        /** 密码 */
        String password = sSh.getPassword();

        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        session.setPassword(password);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no"); // 不验证 HostKey
        session.setConfig(config);
        try {
            session.connect();
        } catch (Exception e) {
            if (session.isConnected()) {
                session.disconnect();
            }
            log.error("连接服务器失败,请检查主机[" + host + "],端口[" + port
                    + "],用户名[" + username + "],端口[" + port
                    + "]是否正确,以上信息正确的情况下请检查网络连接是否正常或者请求被防火墙拒绝.");
        }

        Channel channel = session.openChannel("sftp");
        try {
            channel.connect();
        } catch (Exception e) {
            if (channel.isConnected()) {
                channel.disconnect();
            }
            log.error("连接服务器失败,请检查主机[" + host + "],端口[" + port
                    + "],用户名[" + username + "],密码是否正确,以上信息正确的情况下请检查网络连接是否正常或者请求被防火墙拒绝.");
        }
        ChannelSftp sftp = (ChannelSftp) channel;

        sftp.cd(path);//进入到指定目录下
        InputStream inputStream = new ByteArrayInputStream(codes.getBytes());
        sftp.put(inputStream, fileName);
    }

    /**
     * 执行命令
     *
     * @param cmd 命令
     */
    @Override
    public void shell(SSh sSh, String cmd, WSTools wsTools) throws JSchException, IOException {
        PipedOutputStream pipedOutputStream = null;
        JSch jsch = new JSch();
        String key = "test";
        Session session = null;
        if (connectors.containsKey(key)) {
            session = connectors.get(key);
        }

        if (session == null || !session.isConnected()) {
            session = jsch.getSession(sSh.getUser(), sSh.getHost(), sSh.getPort());
            session.setPassword(sSh.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(50000);
            connectors.put(key, session);
        }

        Channel channel = session.openChannel("shell");
        channels.put(key + "channels", channel);
        PipedInputStream pipedInputStream = new PipedInputStream();
        pipedOutputStream = new PipedOutputStream();
        pipedInputStream.connect(pipedOutputStream);
        channel.setInputStream(pipedInputStream);
        PrintWriter sshout = new PrintWriter(pipedOutputStream, true);

        // 创建输出通道
        pipedInputStream = new PipedInputStream();
        pipedOutputStream = new PipedOutputStream();
        pipedInputStream.connect(pipedOutputStream);
        channel.setOutputStream(pipedOutputStream);
        Scanner scan = new Scanner(pipedInputStream, "UTF-8");
        channel.connect(3 * 1000);
        sshout.println("");
        sshout.flush();

        sshout.println(cmd);
        sshout.println("\n\n");
        sshout.flush();
        StringBuffer stringBuffer = new StringBuffer();
        String line = null;
        while (scan.hasNext()) {
            line = scan.nextLine();
            System.out.println(line);
            wsTools.send(line);
            stringBuffer.append(line);
            stringBuffer.append("\n");
            if (line.trim().equals("pitop@pitop:~$")) {
                break;
            }
        }
        String result = stringBuffer.toString();
        log.debug(result);
        sshout.println("echo $?\n");
        sshout.flush();

        if (scan.hasNext()) {
            do {
                line = scan.nextLine();
                log.debug(line);
            } while (!line.matches("^[0-9]+$"));
        }
    }
}
