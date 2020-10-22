package com.aicode.project.mq.producer;

import com.aicode.project.entity.ProjectModel;
import com.aicode.project.message.ProjectModelMessage;
import com.aicode.project.mq.ProjectModelProducer;
import com.aicode.project.mq.ProjectModelSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 模块 消息生产 实现
 */
@Slf4j
@Service
public class ProjectModelProducerImpl implements ProjectModelProducer {
    @Autowired
    private ProjectModelSink projectModelSink;

    /**
     * 创建 ProjectModel
     *
     * @param projectModel 账户 的实体类
     */
    @Override
    public void build(ProjectModel projectModel) {
        String linkId = RandomStringUtils.random(32, true, true);
        ProjectModelMessage projectModelMessage = new ProjectModelMessage();
        BeanUtils.copyProperties(projectModel, projectModelMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(projectModelMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, projectModelMessage.getCode());
        projectModelSink.buildProjectModelOutput().send(messageBuilder.build());
    }
}
