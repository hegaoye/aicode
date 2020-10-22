package com.aicode.project.mq.producer;

import com.aicode.project.entity.ProjectFramwork;
import com.aicode.project.message.ProjectFramworkMessage;
import com.aicode.project.mq.ProjectFramworkProducer;
import com.aicode.project.mq.ProjectFramworkSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 项目应用技术 消息生产 实现
 */
@Slf4j
@Service
public class ProjectFramworkProducerImpl implements ProjectFramworkProducer {
    @Autowired
    private ProjectFramworkSink projectFramworkSink;

    /**
     * 创建 ProjectFramwork
     *
     * @param projectFramwork 账户 的实体类
     */
    @Override
    public void build(ProjectFramwork projectFramwork) {
        String linkId = RandomStringUtils.random(32, true, true);
        ProjectFramworkMessage projectFramworkMessage = new ProjectFramworkMessage();
        BeanUtils.copyProperties(projectFramwork, projectFramworkMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(projectFramworkMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, projectFramworkMessage.getCode());
        projectFramworkSink.buildProjectFramworkOutput().send(messageBuilder.build());
    }
}
