/*
 * aicode
 */
package com.aicode.account.service;

import com.aicode.account.dao.AccountDAO;
import com.aicode.account.dao.mapper.AccountMapper;
import com.aicode.account.entity.Account;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

    @Autowired
    private UidGenerator uidGenerator;

    @Transactional
    @Override
    public boolean save(Account entity) {
        entity.setId(uidGenerator.getUID());
        entity.setCode(String.valueOf(entity.getId()));

        return super.save(entity);
    }

}


