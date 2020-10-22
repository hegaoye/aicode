/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.account.service;

import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aicode.account.dao.AccountDAO;
import com.aicode.account.dao.mapper.AccountMapper;
import com.aicode.account.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;


/**
 * 账户
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Autowired
    private AccountDAO _accountDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(Account entity) {
//        entity.setId(String.valueOf(uidGenerator.getUID()));
        return super.save(entity);
    }

    /**
     * 分页查询 投注项水位
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<Account>
     */
    @Override
    public List<Account> list(QueryWrapper<Account> queryWrapper, int offset, int limit) {
        return _accountDAO.list(queryWrapper, offset, limit);
    }
}


