package com.aicode.project.mq.producer;

import com.aicode.project.entity.ProjectModelClass;
import com.aicode.project.message.ProjectModelClassMessage;
import com.aicode.project.mq.ProjectModelClassProducer;
import com.aicode.project.mq.ProjectModelClassSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 模块下的类 消息生产 实现
 */
@Slf4j
@Service
public class ProjectModelClassProducerImpl implements ProjectModelClassProducer {
    @Autowired
    private ProjectModelClassSink projectModelClassSink;

    /**
     * 创建 ProjectModelClass
     *
     * @param projectModelClass 账户 的实体类
     */
    @Override
    public void build(ProjectModelClass projectModelClass) {
        String linkId = RandomStringUtils.random(32, true, true);
        ProjectModelClassMessage projectModelClassMessage = new ProjectModelClassMessage();
        BeanUtils.copyProperties(projectModelClass, projectModelClassMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(projectModelClassMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, projectModelClassMessage.getCode());
        projectModelClassSink.buildProjectModelClassOutput().send(messageBuilder.build());
    }
}
