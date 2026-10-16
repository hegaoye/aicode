/*
 * aicode
 */
package com.aicode.account.ctrl;

import com.aicode.account.entity.Account;
import com.aicode.account.service.AccountService;
import com.aicode.account.vo.AccountPublicVO;
import com.aicode.account.vo.AccountSaveVO;
import com.aicode.account.vo.AccountVO;
import com.aicode.core.PageVO;
import com.aicode.core.R;
import com.aicode.core.enums.Constants;
import com.aicode.core.tools.JwtToken;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 账户
 *
 * @author aicode
 */

/**
 * @deprecated 前端未调用, 待人工评估后删除 (JS 静态扫描未发现引用).
 */
@Slf4j
@RestController
@RequestMapping("/account")
@Tag(name = "账户控制器", description = "账户控制器")
@Deprecated
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
    public R<PageVO<AccountPublicVO>> list(Integer curPage, Integer pageSize) {
        // 入参兜底：与 2026-10-18-project-list-real-pagination 给 /project/list 的
        // 口径完全一致（两个分页端点行为必须相同，否则前端两个页面行为不同）。
        // 同时消除拆箱 NPE —— Page(long, long) 收 long，传 Integer null 会 NPE。
        final int effectiveCurPage = (curPage == null || curPage <= 0) ? 1 : curPage;
        final int effectivePageSize = (pageSize == null || pageSize <= 0) ? 10 : pageSize;
        IPage<Account> page = new Page<>(effectiveCurPage, effectivePageSize);

        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        long total = accountService.count(queryWrapper);
        PageVO<AccountPublicVO> accountVOPageVO = new PageVO<>();
        accountVOPageVO.setCurPage(effectiveCurPage);
        accountVOPageVO.setPageSize(effectivePageSize);
        if (total > 0) {
            queryWrapper.lambda().orderByDesc(Account::getId);
            IPage<Account> accountPage = accountService.page(page, queryWrapper);

            // 出参用 AccountPublicVO（3 字段 id / code / account）而非 AccountVO ——
            // 后者含 password（Md5 无盐存储，可离线爆破），作为列表响应即是凭据泄漏。
            // 同 Controller 的 loadByCode 已在用 AccountPublicVO，此处复用而非新建。
            //
            // 中间不再经 AccountPageVO：它与 AccountVO 字段完全相同（同样含 password），
            // 这一层转换没裁切任何东西，只是多一次 JSON 往返；且 password 在中间对象
            // 里存在过。去掉它从结构上消除"将来误复用 AccountPageVO 导致泄漏回归"。
            accountVOPageVO.setTotalRow(accountPage.getTotal());
            accountVOPageVO.setRecords(
                    JSON.parseArray(JSON.toJSONString(accountPage.getRecords()), AccountPublicVO.class));
            accountVOPageVO.setTotalPage((int) Math.ceil((double) accountPage.getTotal() / effectivePageSize));
            log.debug(JSON.toJSONString(page));
        }
        log.debug("查询账户信息集合-{}", accountVOPageVO);
        return R.success(accountVOPageVO);
    }


    @Operation(summary = "修改Account", description = "修改Account")
    @PutMapping("/modify")
    public R<Boolean> modify(@RequestBody AccountVO accountVO) {
        Account newAccount = new Account();
        BeanUtils.copyProperties(accountVO, newAccount);
        boolean isUpdated = accountService.update(newAccount, new LambdaQueryWrapper<Account>()
                .eq(Account::getId, accountVO.getId()));
        // R<Boolean> 而非裸 boolean：裸出参绕过 R 的 code/info/success 三元组，
        // 前端的 unwrapSuccess 无从工作，该端点只能被特殊处理。
        return R.success(isUpdated);
    }


    @Operation(summary = "删除Account", description = "删除Account")
    @Parameters({
            @Parameter(name = "id", description = ""),
            @Parameter(name = "code", description = "账户编码")
    })
    @DeleteMapping("/delete")
    public R<String> delete(AccountVO _accountVO) {
        Account newAccount = new Account();
        BeanUtils.copyProperties(_accountVO, newAccount);
        accountService.remove(new LambdaQueryWrapper<Account>()
                .eq(Account::getId, _accountVO.getId()));
        return R.success("删除成功");
    }


    /**
     * M3-D：按 code 加载账户信息，profile 页面消费集合。
     *
     * 详见 `openspec/changes/2026-10-17-account-load-and-menu/specs/aicode-web-account/spec.md`：
     * - 返 `R<AccountPublicVO>`（3 字段 `id` / `code` / `account`，不含 `password` 字段）
     * - `code == null` 早返 `R.success()`（沿用 M3-A-1 的 R 包络机制，避免 NPE
     *   与"找不到 vs 服务异常"语义混淆）
     * - 鉴权：依赖全局 `LoginInterceptor`（与其他 /account 端点同组）
     * - 用 `AccountPublicVO` 装配而非 `AccountVO` —— 杜绝 password 字段泄漏
     *   到前端 bundle
     */
    @Operation(summary = "按 code 加载账户信息", description = "按 code 加载账户信息（不含密码）")
    @Parameters({
            @Parameter(name = "code", description = "账户编码（不传时从 token claim 抽 accountCode）", required = false),
            @Parameter(name = "token", hidden = true)
    })
    @GetMapping("/load")
    public R<AccountPublicVO> loadByCode(@RequestParam(value = "code", required = false) String code,
                                       @Parameter(hidden = true) String token) {
        // M3-D 实施偏差（design D2 未充分考虑前端 jwtutil 缺位）：
        // - code 入参改为可选
        // - 不传 code 时后端从 token claim (Constants.AccountCode.val.toString()) 抽
        //   accountCode 自己查账户（与 ProjectController.list 同模式 line 260）
        // - 这样前端用 `?code=` 或不传 `?code=` 都行，spec 承诺的"前端能拿账户详情"成立
        if (code == null || code.isEmpty()) {
            if (token == null || token.isEmpty() || "null".equals(token)) {
                return R.success();
            }
            code = JwtToken.getTokenValue(token, Constants.AccountCode.val.toString());
            if (code == null || code.isEmpty()) {
                return R.success();
            }
        }
        Account account = accountService.getOne(new LambdaQueryWrapper<Account>()
                .eq(Account::getCode, code));
        if (account == null) {
            return R.success();
        }
        AccountPublicVO publicVo = new AccountPublicVO();
        // 用 BeanUtils.copyProperties 把 4 字段 Account → 3 字段 target：
        // AccountPublicVO 不含 password setter，password 字段自动丢弃
        BeanUtils.copyProperties(account, publicVo);
        log.debug(JSON.toJSONString(publicVo));
        return R.success(publicVo);
    }

}
