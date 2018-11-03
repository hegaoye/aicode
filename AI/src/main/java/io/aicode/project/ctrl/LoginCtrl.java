package io.aicode.project.ctrl;

import com.alibaba.fastjson.JSON;
import com.jcraft.jsch.*;
import io.aicode.base.interceptor.WSClientManager;
import io.aicode.base.tools.WSTools;
import io.aicode.core.base.BaseCtrl;
import io.aicode.core.base.JwtToken;
import io.aicode.core.common.Constants;
import io.aicode.core.entity.BeanRet;
import io.aicode.core.exceptions.BaseException;
import io.aicode.core.tools.Md5;
import io.aicode.core.tools.SSH2;
import io.aicode.core.tools.SSHResInfo;
import io.aicode.project.entity.Account;
import io.aicode.project.entity.SSh;
import io.aicode.project.service.AccountSV;
import io.aicode.project.service.SShSV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.WebSocketSession;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;


/**
 * 登陆
 *
 * @author lixin
 */
@Slf4j
@Controller
@RequestMapping("/login")
@Api(value = "登陆控制器", description = "登陆控制器")
public class LoginCtrl extends BaseCtrl {

    @Resource
    private AccountSV accountSV;

    @Resource
    private WSClientManager wsClientManager;

    @Resource
    private SShSV sShSV;

    /**
     * 登陆
     *
     * @param account  账户
     * @param password 密码
     * @return
     */
    @ApiOperation(value = "查询一个详情信息", notes = "查询一个详情信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "账户", required = true, paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, paramType = "query")
    })
    @GetMapping(value = "/signin")
    @ResponseBody
    public BeanRet signin(String account, String password, HttpServletResponse response) {
        try {
            Assert.hasText(account, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(password, BaseException.BaseExceptionEnum.Empty_Param.toString());

            Map<String, Object> map = new HashedMap();
            map.put("account", account);
            map.put("password", Md5.md5(password));
            Account accountObj = accountSV.load(map);
            String token = null;
            if (accountObj != null) {
                token = JwtToken.createToken(Constants.AccountCode.val.toString(), accountObj.getCode());
            } else {
                return BeanRet.create();
            }
//            CookieTools.INSTANCE.addCookie((String) Constants.sessionid.val, token, Constants.Domain, response);    //写入cookie
            logger.info(JSON.toJSONString(accountObj));
            return BeanRet.create(true, "", token);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create("验证失败");
        }
    }


    /**
     * 注册
     *
     * @return BeanRet
     */
    @ApiOperation(value = "注册", notes = "注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "账户", required = true, paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, paramType = "query")
    })
    @PostMapping("/reg")
    @ResponseBody
    public BeanRet reg(@ApiIgnore Account account) {
        try {
            Assert.hasText(account.getAccount(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(account.getPassword(), BaseException.BaseExceptionEnum.Empty_Param.toString());
            accountSV.save(account);
            return BeanRet.create(true, "注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "注册失败");
        }
    }


    @ApiOperation(value = "gencode", notes = "gencode")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "codes", value = "编码", required = true, paramType = "query")
    })
    @PostMapping("/gencode")
    @ResponseBody
    public BeanRet genCode(String codes) {
        codes = "from gpiozero import LED\n" +
                "import time\n" +
                "\n" +
                "led=LED(4)\n" +
                "while True:\n" +
                "    led.on()\n" +
                "    print(\"on....\")\n" +
                "    time.sleep(1)\n" +
                "    led.off()\n" +
                "    time.sleep(1)";
        System.out.println(codes);
        try {
            sShSV.close(sshout, channel);
            SSh sSh = new SSh("192.168.1.37", 22, "pi", "0");
            sShSV.sftpUpload(codes, "led.py", "/home/test/", sSh);
            WebSocketSession webSocketSession = wsClientManager.get("192.168.1.95");
            sShSV.shell(sSh, "python /home/test/led.py", new WSTools(webSocketSession));
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BeanRet.create(true, "");
    }


    @ApiOperation(value = "停止程序", notes = "停止程序")
    @GetMapping("/stop")
    @ResponseBody
    public BeanRet stop() {
        sShSV.close(sshout, channel);
        return BeanRet.create(true, "");
    }


    @ApiOperation(value = "ssh", notes = "ssh")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cmd", value = "cmd", required = true, paramType = "query")
    })
    @PostMapping("/ssh")
    @ResponseBody
    public BeanRet ssh(String cmd) {
        String result = null;
        try {
            result = test(cmd);

        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BeanRet.create(true, "", result);
    }


    public void exec(String cmd) {
        SSH2 helper = null;
        try {
            helper = new SSH2("192.168.1.105", 22, "pi", "0");
            SSHResInfo resInfo = helper.sendCmd(cmd);
            System.out.println(resInfo.toString());
            helper.close();
        } catch (JSchException e) {
            e.printStackTrace();
            helper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    Session session = null;
    Channel channel = null;
    PrintWriter sshout;  // SSH 輸出端
    Scanner scan = null;

    public String test(String cmd) throws Exception {
        WebSocketSession webSocketSession = wsClientManager.get("192.168.1.95");
//        WebSocketSession webSocketSession = wsClientManager.get(request.getRemoteHost());
        WSTools wsTools = new WSTools(webSocketSession);
        PipedOutputStream pipedOutputStream = null;
        JSch jsch = new JSch();
        if (session == null) {
            session = jsch.getSession("pi", "192.168.1.37", 22);
            session.setPassword("0");
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(50000);
        }

        channel = session.openChannel("shell");
        PipedInputStream pipedInputStream;

        pipedInputStream = new PipedInputStream();
        pipedOutputStream = new PipedOutputStream();
        pipedInputStream.connect(pipedOutputStream);
        channel.setInputStream(pipedInputStream);
        sshout = new PrintWriter(pipedOutputStream, true);

        // 创建输出通道
        pipedInputStream = new PipedInputStream();
        pipedOutputStream = new PipedOutputStream();
        pipedInputStream.connect(pipedOutputStream);
        channel.setOutputStream(pipedOutputStream);
        scan = new Scanner(pipedInputStream, "UTF-8");
        channel.connect(3 * 1000);
        sshout.println("");
        sshout.flush();

        if (cmd.contains("su")) {
            switchRoot("0");
            return "root";
        }
        sshout.print(cmd);
        sshout.print("\n\n");
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
            if (line.trim().contains("password")) {
                break;
            }
        }
        String result = stringBuffer.toString();
        log.debug(result);
        sshout.print("echo $?\n");
        sshout.flush();

        if (scan.hasNext()) {
            do {
                line = scan.nextLine();
            } while (!line.matches("^[0-9]+$"));
        }
        return result;
    }


    public void switchRoot(String password) {
        String line;

        // 搜尋登入成功的提示字串
        sshout.print("su -\n");
        sshout.flush();

        // 搜尋密碼輸入的提示字串
        scan.findWithinHorizon("Password: ", 0);
        sshout.print(password);
        sshout.print('\n');
        sshout.flush();

        // 檢查是否登入成功
        sshout.print("echo $?\n");
        sshout.flush();
        do {
            line = scan.nextLine();
        } while (!line.matches("^[0-9]+$"));

    }


}
