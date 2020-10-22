package com.aicode.account.mq.consumer;

import com.aicode.account.entity.Account;
import com.aicode.account.message.AccountMessage;
import com.aicode.account.mq.AccountSink;
import com.aicode.account.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 账户 消费入口
 */
@Slf4j
@Service
public class AccountConsumer {

    @Autowired
    private AccountService _accountService;

    /**
     * 监听 创建 account 数据消费
     *
     * @param _accountMessage 玩家彩票id集合
     */
    @StreamListener(AccountSink.buildAccountInput)
    public void autoReduceOddsInput(@Payload AccountMessage _accountMessage) {
        log.info("{}", _accountMessage);
        try {
            Account _account = new Account();
            BeanUtils.copyProperties(_accountMessage, _account);
            _accountService.save(_account);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", _accountMessage, e.getMessage());
        }

    }

}
