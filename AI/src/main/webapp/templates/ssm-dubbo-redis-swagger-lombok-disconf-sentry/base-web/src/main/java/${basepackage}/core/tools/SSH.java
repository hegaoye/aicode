/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于AI-Code.
 *
 */

package ${basePackage}.core.tools;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * java ssh登录linux以后的一些操作方式
 * Created by lixin on 2017/10/18.
 */
public class SSH {
    private final static Logger log = LoggerFactory.getLogger(SSH.class);

    public static void main(String[] args) {
        try {
            //使用目标服务器机上的用户名和密码登陆
            SSH helper = new SSH("47.91.246.115", 1989, "root", "!@#$%^{Tutors@2017Ponddy.Com}");
            try {
                SSHResInfo resInfo = helper.sendCmd("unzip -d /home/env/ /home/env/1.zip");
                System.out.println(resInfo.toString());
                //System.out.println(helper.deleteRemoteFIleOrDir(command));
                //System.out.println(helper.detectedFileExist(command));
                helper.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (JSchException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String charset = Charset.defaultCharset().toString();
    private Session session;

    public SSH(String host, Integer port, String user, String password) throws JSchException {
        connect(host, port, user, password);
    }

    /**
     * 连接sftp服务器
     *
     * @param host     远程主机ip地址
     * @param port     sftp连接端口，null 时为默认端口
     * @param user     用户名
     * @param password 密码
     * @return
     * @throws JSchException
     */
    private Session connect(String host, Integer port, String user, String password) throws JSchException {
        try {
            JSch jsch = new JSch();
            if (port != null) {
                session = jsch.getSession(user, host, port.intValue());
            } else {
                session = jsch.getSession(user, host);
            }
            session.setPassword(password);
            //设置第一次登陆的时候提示，可选值:(ask | yes | no)
            session.setConfig("StrictHostKeyChecking", "no");
            //30秒连接超时
            session.connect(50000);


        } catch (JSchException e) {
            e.printStackTrace();
            System.out.println("SFTPUitl 获取连接发生错误");
            throw e;
        }
        return session;
    }

    public SSHResInfo sendCmd(String command) throws Exception {
        return sendCmd(command, 200);
    }

    /*
    * 执行命令，返回执行结果
    * @param command 命令
    * @param delay 估计shell命令执行时间
    * @return String 执行命令后的返回
    * @throws IOException
    * @throws JSchException
    */
    public SSHResInfo sendCmd(String command, int delay) throws Exception {
        if (delay < 50) {
            delay = 50;
        }
        SSHResInfo result = null;
        byte[] tmp = new byte[1024]; //读数据缓存
        StringBuffer strBuffer = new StringBuffer();  //执行SSH返回的结果
        StringBuffer errResult = new StringBuffer();

        Channel channel = session.openChannel("exec");
        ChannelExec ssh = (ChannelExec) channel;
        //返回的结果可能是标准信息,也可能是错误信息,所以两种输出都要获取
        //一般情况下只会有一种输出.
        //但并不是说错误信息就是执行命令出错的信息,如获得远程java JDK版本就以
        //ErrStream来获得.
        InputStream stdStream = ssh.getInputStream();
        InputStream errStream = ssh.getErrStream();

        ssh.setCommand(command);
        ssh.connect();

        try {


            //开始获得SSH命令的结果
            while (true) {
                //获得错误输出
                while (errStream.available() > 0) {
                    int i = errStream.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    errResult.append(new String(tmp, 0, i));
                }

                //获得标准输出
                while (stdStream.available() > 0) {
                    int i = stdStream.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    strBuffer.append(new String(tmp, 0, i));
                }
                if (ssh.isClosed()) {
                    int code = ssh.getExitStatus();
                    log.info("exit-status: " + code);
                    result = new SSHResInfo(code, strBuffer.toString(), errResult.toString());
                    break;
                }
                try {
                    Thread.sleep(delay);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        } finally {
            // TODO: handle finally clause
            channel.disconnect();
        }

        return result;
    }

    /**
     * @param in
     * @param charset
     * @return
     */
    private String processStream(InputStream in, String charset) throws Exception {
        byte[] buf = new byte[1024];
        StringBuilder sb = new StringBuilder();
        while (in.read(buf) != -1) {
            sb.append(new String(buf, charset));
        }
        return sb.toString();
    }

    public boolean deleteRemoteFIleOrDir(String remoteFile) {
        ChannelSftp channel = null;
        try {
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            SftpATTRS sftpATTRS = channel.lstat(remoteFile);
            if (sftpATTRS.isDir()) {
                //目录
                log.debug("remote File:dir");
                channel.rmdir(remoteFile);
                return true;
            } else if (sftpATTRS.isReg()) {
                //文件
                log.debug("remote File:file");
                channel.rm(remoteFile);
                return true;
            } else {
                log.debug("remote File:unkown");
                return false;
            }
        } catch (JSchException e) {
            if (channel != null) {
                channel.disconnect();
                session.disconnect();
            }
            log.error("error", e);
            return false;
        } catch (SftpException e) {
            log.info("meg" + e.getMessage());
            log.error("SftpException", e);
            return false;
        }

    }

    /**
     * 判断linux下 某文件是否存在
     */
    public boolean detectedFileExist(String remoteFile) {

        ChannelSftp channel = null;
        try {
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            SftpATTRS sftpATTRS = channel.lstat(remoteFile);
            if (sftpATTRS.isDir() || sftpATTRS.isReg()) {
                //目录 和文件
                log.info("remote File:dir");
                return true;
            } else {
                log.info("remote File:unkown");
                return false;
            }
        } catch (JSchException e) {
            if (channel != null) {
                channel.disconnect();
                session.disconnect();
            }
            return false;
        } catch (SftpException e) {
            log.error(e.getMessage());
        }
        return false;
    }


    /**
     * 用完记得关闭，否则连接一直存在，程序不会退出
     */
    public void close() {
        if (session.isConnected()) {
            session.disconnect();
        }
    }

}
