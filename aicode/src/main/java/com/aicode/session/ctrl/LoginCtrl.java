package com.aicode.session.ctrl;

import com.aicode.account.entity.Account;
import com.aicode.account.service.AccountService;
import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.core.enums.Constants;
import com.aicode.core.tools.JwtToken;
import com.aicode.core.tools.PasswordHasher;
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
    public R<String> signin(String account, String password, HttpServletResponse response) {
        try {
            Assert.hasText(account, BaseException.BaseExceptionEnum.Empty_Param.toString());
            Assert.hasText(password, BaseException.BaseExceptionEnum.Empty_Param.toString());

            // 查询条件**只含账户名**：带盐散列每次结果不同，相同口令两次散列不相等，
            // `WHERE password = ?` 这种等值匹配在算法层面就不可行；且它还会让散列值
            // 出现在 SQL 日志里（AGENTS §10 已登记该坑）。
            Account accountObj = accountService.getOne(new LambdaQueryWrapper<Account>()
                    .eq(Account::getAccount, account));

            // 「账户不存在」与「口令错误」返回同样的空 data，不签发 token ——
            // 防账户枚举（沿用既有 auth-account Req 的约定）。
            if (accountObj == null) {
                return R.success();
            }
            if (!PasswordHasher.matches(password, accountObj.getPassword())) {
                return R.success();
            }

            // 透明升级：存量弱散列（无盐 MD5）在**校验通过后**改写为 BCrypt。
            // 顺序必须是「先校验、后改写」—— 口令错误时改写会把合法用户锁死
            // （攻击者可用任意错误口令把口令替换成他不知道的值）。
            if (PasswordHasher.needsUpgrade(accountObj.getPassword())) {
                Account toUpdate = new Account();
                toUpdate.setId(accountObj.getId());
                toUpdate.setPassword(PasswordHasher.hash(password));
                accountService.updateById(toUpdate);
                log.info("账户口令已从旧格式升级为 BCrypt, code:{}", accountObj.getCode());
            }

            String token = JwtToken.createToken(Constants.AccountCode.val.toString(), accountObj.getCode());
            log.info(JSON.toJSONString(accountObj));
            return R.success(token);
        } catch (Exception e) {
            log.error("异常", e);
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
    public R<Void> reg(@Parameter(hidden = true) Account account) {
        Assert.hasText(account.getAccount(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        Assert.hasText(account.getPassword(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        // 散列后再落库 —— 此前直接 save(account) 会把**明文口令**写进库
        // （比 MD5 更严重）。散列在实体入参上就地替换，避免调用方拿到散列后的值。
        account.setPassword(PasswordHasher.hash(account.getPassword()));
        accountService.save(account);
        return R.success();
    }

}
