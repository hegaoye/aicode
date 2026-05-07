/*
 * aicode
 */
package com.aicode.account.ctrl;

import com.aicode.account.entity.Account;
import com.aicode.account.service.AccountService;
import com.aicode.account.vo.AccountPageVO;
import com.aicode.account.vo.AccountSaveVO;
import com.aicode.account.vo.AccountVO;
import com.aicode.core.BaseException;
import com.aicode.core.PageVO;
import com.aicode.core.R;
import com.aicode.core.enums.Constants;
import com.aicode.core.tools.JwtToken;
import com.aicode.core.tools.Md5;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 账户
 *
 * @author aicode
 */
@Slf4j
@RestController
@RequestMapping("/account")
@Tag(name = "账户控制器", description = "账户控制器")
public class AccountController {
    @Autowired
    private AccountService accountService;

    
    @Operation(summary = "创建Account", description = "创建Account")
    @PostMapping("/build")
    public AccountSaveVO build(@RequestBody AccountSaveVO accountSaveVO) {
        if (null == accountSaveVO) {
            return null;
        }
        Account newAccount = new Account();
        BeanUtils.copyProperties(accountSaveVO, newAccount);

        accountService.save(newAccount);

        accountSaveVO = new AccountSaveVO();
        BeanUtils.copyProperties(newAccount, accountSaveVO);
        log.debug(JSON.toJSONString(accountSaveVO));
        return accountSaveVO;
    }

    @Operation(summary = "查询Account信息集合", description = "查询Account信息集合")
    @Parameters({
            @Parameter(name = "curPage", description = "当前页", required = true),
            @Parameter(name = "pageSize", description = "分页大小", required = true),
    })
    @GetMapping(value = "/list")
    public PageVO<AccountVO> list(Integer curPage, Integer pageSize) {
        IPage<Account> page = new Page<>(curPage, pageSize);

        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        long total = accountService.count(queryWrapper);
        PageVO<AccountVO> accountVOPageVO = new PageVO<>();
        if (total > 0) {
            queryWrapper.lambda().orderByDesc(Account::getId);
            IPage<Account> accountPage = accountService.page(page, queryWrapper);
            List<AccountPageVO> accountPageVOList = JSON.parseArray(JSON.toJSONString(accountPage.getRecords()), AccountPageVO.class);

            accountVOPageVO.setTotalRow(accountPage.getTotal());
            accountVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(accountPageVOList), AccountVO.class));
            log.debug(JSON.toJSONString(page));
        }
        log.debug("查询账户信息集合-{}", accountVOPageVO);
        return accountVOPageVO;
    }


    
    @Operation(summary = "修改Account", description = "修改Account")
    @PutMapping("/modify")
    public boolean modify(@RequestBody AccountVO accountVO) {
        Account newAccount = new Account();
        BeanUtils.copyProperties(accountVO, newAccount);
        boolean isUpdated = accountService.update(newAccount, new LambdaQueryWrapper<Account>()
                .eq(Account::getId, accountVO.getId()));
        return isUpdated;
    }


    
    @Operation(summary = "删除Account", description = "删除Account")
    @Parameters({
            @Parameter(name = "id", description = ""),
            @Parameter(name = "code", description = "账户编码")
    })
    @DeleteMapping("/delete")
    public R delete(@Parameter(hidden = true) AccountVO _accountVO) {
        Account newAccount = new Account();
        BeanUtils.copyProperties(_accountVO, newAccount);
        accountService.remove(new LambdaQueryWrapper<Account>()
                .eq(Account::getId, _accountVO.getId()));
        return R.success("删除成功");
    }

}
