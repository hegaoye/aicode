package io.aicode.project.ctrl;

import com.alibaba.fastjson.JSON;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import io.aicode.core.base.BaseCtrl;
import io.aicode.core.base.JwtToken;
import io.aicode.core.common.Constants;
import io.aicode.core.entity.BeanRet;
import io.aicode.core.exceptions.BaseException;
import io.aicode.core.tools.Md5;
import io.aicode.project.entity.Account;
import io.aicode.project.service.AccountSV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.Scanner;


/**
 * 登陆
 *
 * @author lixin
 */
@Controller
@RequestMapping("/login")
@Api(value = "登陆控制器", description = "登陆控制器")
public class LoginCtrl extends BaseCtrl {

    @Resource
    private AccountSV accountSV;

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
        }
        return BeanRet.create(true, "", result);
    }

    Session session = null;
    Channel channel = null;
    PrintWriter sshout;  // SSH 輸出端
    Scanner scan = null;
    InputStream inputStream = null;

    public String test(String cmd) throws JSchException, IOException {
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

            // 创建输出通道
            pipedInputStream = new PipedInputStream();
            pipedOutputStream = new PipedOutputStream();
            pipedInputStream.connect(pipedOutputStream);
            channel.setOutputStream(pipedOutputStream);
//            inputStream = pipedInputStream;
            scan = new Scanner(pipedInputStream, "UTF-8");
            channel.connect(3 * 1000);
            sshout.println("");
            sshout.flush();

        }

        if (cmd.contains("su")) {
            switchRoot("0");
            return "root";
        }
        sshout.print(cmd);
        sshout.print("\n\n");
        sshout.flush();
        StringBuffer stringBuffer = new StringBuffer();
        boolean flag = true;
//        byte[] tmp = new byte[1024];
//        while (true) {
//            if (stringBuffer.length() > 0) {
//                break;
//            }
//            while (inputStream.available() > 0) {
//                int i = inputStream.read(tmp, 0, 1024);
//                if (i < 0) break;
//                String s = new String(tmp, 0, i);
//                stringBuffer.append(s);
//                stringBuffer.append("\n");
//            }
//            if (channel.isClosed()) {
//                System.out.println("exit-status: " + channel.getExitStatus());
//                break;
//            }
//            try {
//                Thread.sleep(150);
//            } catch (Exception ee) {
//            }
//        }

        String line = null;
        while (scan.hasNext()) {
            line = scan.nextLine();
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
        System.out.println(result);
        sshout.print("echo $?\n");
        sshout.flush();

        do {
            line = scan.nextLine();
        } while (!line.matches("^[0-9]+$"));

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
