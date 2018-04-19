package com.rzhkj.project.ctrl;

import com.beust.jcommander.internal.Maps;
import com.rzhkj.core.base.BaseCtrl;
import com.rzhkj.core.base.JwtToken;
import com.rzhkj.core.common.Constants;
import com.rzhkj.core.entity.BeanRet;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.tools.Md5;
import com.rzhkj.project.entity.Account;
import com.rzhkj.project.service.AccountSV;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;


/**
 * 账户控制器
 *
 * @author lixin
 */
@Controller
@RequestMapping("/account")
@Api(value = "账户控制器", description = "账户控制器")
public class AccountCtrl extends BaseCtrl {

    @Resource
    private AccountSV accountSV;


    /**
     * 修改密码
     *
     * @return BeanRet
     */
    @ApiOperation(value = "修改密码", notes = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "password", value = "密码", required = true, paramType = "query")
    })
    @PutMapping("/modify/password")
    @ResponseBody
    public BeanRet modifyPassword(String password, String token) {
        try {
            Assert.hasText(password, BaseException.BaseExceptionEnum.Empty_Param.toString());
            String accountCode = JwtToken.getTokenValue(token, Constants.AccountCode.val.toString());
            Map<String, Object> map = Maps.newHashMap();
            map.put("code", accountCode);
            Account accountLoad = accountSV.load(map);
            accountLoad.setPassword(Md5.md5(password));
            accountSV.update(accountLoad);
            return BeanRet.create(true, "注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return BeanRet.create(false, "注册失败");
        }
    }
}
