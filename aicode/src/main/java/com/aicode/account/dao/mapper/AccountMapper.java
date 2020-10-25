package com.aicode.account.dao.mapper;

import com.aicode.account.entity.Account;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * Account mapper
 * 直接与xml映射
 *
 * @author hegaoye
 */
@Repository
public interface AccountMapper extends BaseMapper<Account> {

}
