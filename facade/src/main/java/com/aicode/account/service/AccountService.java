/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.account.service;

import com.aicode.account.entity.Account;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 账户
 *
 * @author hegaoye
 */
public interface AccountService extends IService<Account> {

    /**
     * 分页查询
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<Account>
     */
    List<Account> list(QueryWrapper<Account> queryWrapper, int offset, int limit);
}


