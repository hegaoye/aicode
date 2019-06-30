package io.aicode.project.ctrl;

import com.alibaba.fastjson.JSON;
import io.aicode.base.BaseCtrl;
import io.aicode.base.JwtToken;
import io.aicode.base.common.Constants;
import io.aicode.base.core.BeanRet;
import io.aicode.base.exceptions.BaseException;
import io.aicode.base.interceptor.WSClientManager;
import io.aicode.base.tools.Md5;
import io.aicode.project.entity.Account;
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
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


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


}
