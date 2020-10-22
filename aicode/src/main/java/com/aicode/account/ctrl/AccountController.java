/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.account.ctrl;

import com.aicode.account.entity.Account;
import com.aicode.account.service.AccountService;
import com.aicode.account.vo.AccountPageVO;
import com.aicode.account.vo.AccountSaveVO;
import com.aicode.account.vo.AccountVO;
import com.aicode.core.entity.Page;
import com.aicode.core.entity.PageVO;
import com.aicode.core.entity.R;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 账户
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/_account")
@Slf4j
@Api(value = "账户控制器", tags = "账户控制器")
public class AccountController {
    @Autowired
    private AccountService _accountService;


    /**
     * 创建 账户
     *
     * @return R
     */
    @ApiOperation(value = "创建Account", notes = "创建Account")
    @PostMapping("/build")
    public AccountSaveVO build(@ApiParam(name = "创建Account", value = "传入json格式", required = true)
                                   @RequestBody AccountSaveVO _accountSaveVO) {
        if (null == _accountSaveVO) {
            return null;
        }
        Account newAccount = new Account();
        BeanUtils.copyProperties(_accountSaveVO, newAccount);

        _accountService.save(newAccount);

        _accountSaveVO = new AccountSaveVO();
        BeanUtils.copyProperties(newAccount, _accountSaveVO);
        log.debug(JSON.toJSONString(_accountSaveVO));
        return _accountSaveVO;
    }


    /**
     * 根据条件code查询账户一个详情信息
     *
     * @param code 账户编码
     * @return AccountVO
     */
    @ApiOperation(value = "创建Account", notes = "创建Account")
    @GetMapping("/load/code/{code}")
    public AccountVO loadByCode(@PathVariable java.lang.String code) {
        if (code == null) {
            return null;
        }
        Account _account = _accountService.getOne(new LambdaQueryWrapper<Account>()
                .eq(Account::getCode, code));
        AccountVO _accountVO = new AccountVO();
        BeanUtils.copyProperties(_account, _accountVO);
        log.debug(JSON.toJSONString(_accountVO));
        return _accountVO;
    }

    /**
     * 查询账户信息集合
     *
     * @return 分页对象
     */
    @ApiOperation(value = "查询Account信息集合", notes = "查询Account信息集合")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curPage", value = "当前页", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", required = true, paramType = "query"),
    })
    @GetMapping(value = "/list")
    public PageVO<AccountVO> list(@ApiIgnore AccountPageVO _accountVO, Integer curPage, Integer pageSize) {
        Page<Account> page = new Page<>(pageSize, curPage);
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        int total = _accountService.count(queryWrapper);
        PageVO<AccountVO> _accountVOPageVO = new PageVO<>();
        if (total > 0) {
            List<Account> _accountList = _accountService.list(queryWrapper, page.genRowStart(), page.getPageSize());
            _accountVOPageVO.setTotalRow(total);
            _accountVOPageVO.setRecords(JSON.parseArray(JSON.toJSONString(_accountList),AccountVO.class));
            log.debug(JSON.toJSONString(page));
        }
        return _accountVOPageVO;
    }


    /**
     * 修改 账户
     *
     * @return R
     */
    @ApiOperation(value = "修改Account", notes = "修改Account")
    @PutMapping("/modify")
    public boolean modify(@ApiParam(name = "修改Account", value = "传入json格式", required = true)
                          @RequestBody AccountVO _accountVO) {
        Account newAccount = new Account();
        BeanUtils.copyProperties(_accountVO, newAccount);
        boolean isUpdated = _accountService.update(newAccount, new LambdaQueryWrapper<Account>()
                .eq(Account::getId, _accountVO.getId()));
        return isUpdated;
    }


    /**
     * 删除 账户
     *
     * @return R
     */
    @ApiOperation(value = "删除Account", notes = "删除Account")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "账户编码", paramType = "query")
    })
    @DeleteMapping("/delete")
    public R delete(@ApiIgnore AccountVO _accountVO) {
        Account newAccount = new Account();
        BeanUtils.copyProperties(_accountVO, newAccount);
        _accountService.remove(new LambdaQueryWrapper<Account>()
                .eq(Account::getId, _accountVO.getId()));
        return R.success("删除成功");
    }

}
