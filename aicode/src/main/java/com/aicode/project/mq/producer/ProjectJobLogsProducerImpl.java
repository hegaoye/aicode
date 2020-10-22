package com.aicode.project.mq.producer;

import com.aicode.project.entity.ProjectJobLogs;
import com.aicode.project.message.ProjectJobLogsMessage;
import com.aicode.project.mq.ProjectJobLogsProducer;
import com.aicode.project.mq.ProjectJobLogsSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 任务日志 消息生产 实现
 */
@Slf4j
@Service
public class ProjectJobLogsProducerImpl implements ProjectJobLogsProducer {
    @Autowired
    private ProjectJobLogsSink projectJobLogsSink;

    /**
     * 创建 ProjectJobLogs
     *
     * @param projectJobLogs 账户 的实体类
     */
    @Override
    public void build(ProjectJobLogs projectJobLogs) {
        String linkId = RandomStringUtils.random(32, true, true);
        ProjectJobLogsMessage projectJobLogsMessage = new ProjectJobLogsMessage();
        BeanUtils.copyProperties(projectJobLogs, projectJobLogsMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(projectJobLogsMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, projectJobLogsMessage.getCode());
        projectJobLogsSink.buildProjectJobLogsOutput().send(messageBuilder.build());
    }
}
