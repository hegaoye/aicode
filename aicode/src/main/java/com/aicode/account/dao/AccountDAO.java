package com.aicode.account.dao;

import com.aicode.account.dao.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Account DAO
 * 数据服务层
 *
 * @author aicode
 */
@Repository
public class AccountDAO {


    /**
     * Account mapper
     */
    @Autowired
    private AccountMapper _accountMapper;


}