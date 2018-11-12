//package com.rzhkj.project.ctrl;
//
//import com.jcraft.jsch.Channel;
//import com.jcraft.jsch.JSch;
//import com.jcraft.jsch.JSchException;
//import com.jcraft.jsch.Session;
//import BeanRet;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.socket.TextMessage;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
///**
// * Created by lixin on 2018/10/1.
// */
//@Controller
//@RequestMapping(value = "/shell")
//@Slf4j
//public class ShellCtrl {
//    /**
//     * 查询一个详情信息
//     *
//     * @return BeanRet
//     */
//    @ApiOperation(value = "查询一个详情信息", notes = "查询一个详情信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "cmd", value = "cmd", paramType = "query")
//    })
//    @GetMapping(value = "/load")
//    @ResponseBody
//    public BeanRet load(String cmd) {
//        try {
//            String result = this.shell(cmd);
//            return BeanRet.create(true, "", (Object) result);
//        } catch (JSchException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return BeanRet.create();
//    }
//
//    Session session = null;
//
//
//    public String shell(String cmd) throws JSchException, IOException {
//        String result = null;
//
//        try {
//            if (session == null) {
//                JSch jsch = new JSch();
//                session = null;
//                session = jsch.getSession("pitop", "192.168.1.220", 22);
//                session.setPassword("0");
//                session.setConfig("StrictHostKeyChecking", "no");
//            }
//            if (!session.isConnected()) {
//                session.connect(50000);
//            }
//            Channel channel = null;
//            if (channel == null) {
//                channel = session.openChannel("shell");
//            }
//
//            channel.connect(3 * 1000);
//
//            InputStream in = new ByteArrayInputStream(cmd.getBytes());
//            channel.setInputStream(in);
//            InputStream inputStream1 = channel.getInputStream();
//            byte[] tmp1 = new byte[1024];
//
//            while (true) {
//                StringBuffer stringBuffer = new StringBuffer();
//                while (inputStream1.available() > 0) {
//                    int i = inputStream1.read(tmp1, 0, 1024);
//                    stringBuffer.append(new String(tmp1, 0, i));
//                }
//                if (stringBuffer.length() > 0) {
//                    result = stringBuffer.toString();
//                    log.debug(result);
//                    return result;
//                }
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        } catch (JSchException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//}
