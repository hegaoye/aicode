package com.aicode.session.ctrl;

import com.aicode.account.entity.Account;
import com.aicode.account.service.AccountService;
import com.aicode.core.entity.R;
import com.aicode.core.enums.Constants;
import com.aicode.core.exceptions.BaseException;
import com.aicode.core.tools.JwtToken;
import com.aicode.core.tools.security.Md5;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;


/**
 * 登陆
 *
 * @author lixin
 */
@Slf4j
@RestController
@RequestMapping("/login")
@Api(value = "登陆控制器", tags = "登陆控制器")
public class LoginCtrl {

    @Resource
    private AccountService accountService;


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
    public R signin(String account, String password, HttpServletResponse response) {
        try {
            Assert.hasText(account, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(password, BaseException.BaseExceptionEnum.Empty_Param.toString());

            Account accountObj = accountService.getOne(new LambdaQueryWrapper<Account>()
                    .eq(Account::getAccount, account)
                    .eq(Account::getPassword, Md5.md5(password)));
            String token = null;
            if (accountObj != null) {
                token = JwtToken.createToken(Constants.AccountCode.val.toString(), accountObj.getCode());
            } else {
                return R.success();
            }
            log.info(JSON.toJSONString(accountObj));
            return R.success(token);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return R.failed("验证失败");
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
    public R reg(@ApiIgnore Account account) {
        Assert.hasText(account.getAccount(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(account.getPassword(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        accountService.save(account);
        return R.success();
    }

}
