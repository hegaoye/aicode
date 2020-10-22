package com.aicode.project.mq.consumer;

import com.aicode.project.entity.ProjectRepositoryAccount;
import com.aicode.project.message.ProjectRepositoryAccountMessage;
import com.aicode.project.mq.ProjectRepositoryAccountSink;
import com.aicode.project.service.ProjectRepositoryAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 版本控制管理 消费入口
 */
@Slf4j
@Service
public class ProjectRepositoryAccountConsumer {

    @Autowired
    private ProjectRepositoryAccountService projectRepositoryAccountService;

    /**
     * 监听 创建 project 数据消费
     *
     * @param projectRepositoryAccountMessage 玩家彩票id集合
     */
    @StreamListener(ProjectRepositoryAccountSink.buildProjectRepositoryAccountInput)
    public void autoReduceOddsInput(@Payload ProjectRepositoryAccountMessage projectRepositoryAccountMessage) {
        log.info("{}", projectRepositoryAccountMessage);
        try {
            ProjectRepositoryAccount projectRepositoryAccount = new ProjectRepositoryAccount();
            BeanUtils.copyProperties(projectRepositoryAccountMessage, projectRepositoryAccount);
            projectRepositoryAccountService.save(projectRepositoryAccount);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", projectRepositoryAccountMessage, e.getMessage());
        }

    }

}
