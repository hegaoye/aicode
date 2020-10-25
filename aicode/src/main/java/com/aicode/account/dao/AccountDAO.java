package com.aicode.account.dao;

import com.aicode.account.dao.mapper.AccountMapper;
import com.aicode.account.entity.Account;
import com.aicode.core.base.BaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Account DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class AccountDAO extends BaseDAO<AccountMapper, Account> {


    /**
     * Account mapper
     */
    @Autowired
    private AccountMapper _accountMapper;


}