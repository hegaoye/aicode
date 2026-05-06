package com.aicode.session.ctrl;

import com.aicode.account.entity.Account;
import com.aicode.account.service.AccountService;
import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.core.enums.Constants;
import com.aicode.core.tools.JwtToken;
import com.aicode.core.tools.Md5;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 登陆
 *
 * @author lixin
 */
@Slf4j
@RestController
@RequestMapping("/login")
@Tag(name = "登陆控制器", description = "登陆控制器")
public class LoginCtrl {

    @Autowired
    private AccountService accountService;


    @Operation(summary = "查询一个详情信息", description = "查询一个详情信息")
    @Parameters(value = {
            @Parameter(name = "account", required = true, description = "账户"),
            @Parameter(name = "password", required = true, description = "密码"),
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
            return R.failed(BaseException.BaseExceptionEnum.Illegal_Param);
        }
    }


    @Operation(summary = "注册", description = "注册")
    @Parameters(value = {
            @Parameter(name = "account", required = true, description = "账户"),
            @Parameter(name = "password", required = true, description = "密码"),
    })
    @PostMapping("/reg")
    public R reg(@Parameter(hidden = true) Account account) {
        Assert.hasText(account.getAccount(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(account.getPassword(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        accountService.save(account);
        return R.success();
    }

}
