package com.aicode.project.mq.producer;

import com.aicode.project.entity.ProjectMap;
import com.aicode.project.message.ProjectMapMessage;
import com.aicode.project.mq.ProjectMapProducer;
import com.aicode.project.mq.ProjectMapSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 项目数据表 消息生产 实现
 */
@Slf4j
@Service
public class ProjectMapProducerImpl implements ProjectMapProducer {
    @Autowired
    private ProjectMapSink projectMapSink;

    /**
     * 创建 ProjectMap
     *
     * @param projectMap 账户 的实体类
     */
    @Override
    public void build(ProjectMap projectMap) {
        String linkId = RandomStringUtils.random(32, true, true);
        ProjectMapMessage projectMapMessage = new ProjectMapMessage();
        BeanUtils.copyProperties(projectMap, projectMapMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(projectMapMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, projectMapMessage.getCode());
        projectMapSink.buildProjectMapOutput().send(messageBuilder.build());
    }
}
