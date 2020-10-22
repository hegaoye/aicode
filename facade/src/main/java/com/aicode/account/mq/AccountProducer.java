package com.aicode.account.mq;

import com.aicode.account.entity.Account;

/**
 * 消息生产 接口
 */
public interface AccountProducer {

    /**
     * 创建 Account
     *
     * @param _account 账户 的实体类
     */
    void build(Account _account);

}
