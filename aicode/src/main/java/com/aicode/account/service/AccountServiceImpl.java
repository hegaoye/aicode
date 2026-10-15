/*
 * aicode
 */
package com.aicode.account.service;

import com.aicode.account.dao.AccountDAO;
import com.aicode.account.dao.mapper.AccountMapper;
import com.aicode.account.entity.Account;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 账户
 *
 * @author aicode
 */
@Slf4j
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Autowired
    private AccountDAO accountDAO;
    @Transactional
    @Override
    public boolean save(Account entity) {
        entity.setCode(String.valueOf(entity.getId()));

        return super.save(entity);
    }

}


