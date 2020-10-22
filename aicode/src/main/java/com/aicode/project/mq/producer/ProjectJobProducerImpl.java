package com.aicode.project.mq.producer;

import com.aicode.project.entity.ProjectJob;
import com.aicode.project.message.ProjectJobMessage;
import com.aicode.project.mq.ProjectJobProducer;
import com.aicode.project.mq.ProjectJobSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 任务 消息生产 实现
 */
@Slf4j
@Service
public class ProjectJobProducerImpl implements ProjectJobProducer {
    @Autowired
    private ProjectJobSink projectJobSink;

    /**
     * 创建 ProjectJob
     *
     * @param projectJob 账户 的实体类
     */
    @Override
    public void build(ProjectJob projectJob) {
        String linkId = RandomStringUtils.random(32, true, true);
        ProjectJobMessage projectJobMessage = new ProjectJobMessage();
        BeanUtils.copyProperties(projectJob, projectJobMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(projectJobMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, projectJobMessage.getCode());
        projectJobSink.buildProjectJobOutput().send(messageBuilder.build());
    }
}
