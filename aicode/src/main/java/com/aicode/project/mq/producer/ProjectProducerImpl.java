package com.aicode.project.mq.producer;

import com.aicode.project.entity.Project;
import com.aicode.project.message.ProjectMessage;
import com.aicode.project.mq.ProjectProducer;
import com.aicode.project.mq.ProjectSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 项目信息 消息生产 实现
 */
@Slf4j
@Service
public class ProjectProducerImpl implements ProjectProducer {
    @Autowired
    private ProjectSink projectSink;

    /**
     * 创建 Project
     *
     * @param project 账户 的实体类
     */
    @Override
    public void build(Project project) {
        String linkId = RandomStringUtils.random(32, true, true);
        ProjectMessage projectMessage = new ProjectMessage();
        BeanUtils.copyProperties(project, projectMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(projectMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, projectMessage.getCode());
        projectSink.buildProjectOutput().send(messageBuilder.build());
    }
}
