package com.aicode.project.mq.producer;

import com.aicode.project.entity.ProjectModule;
import com.aicode.project.message.ProjectModuleMessage;
import com.aicode.project.mq.ProjectModuleProducer;
import com.aicode.project.mq.ProjectModuleSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 项目选择模块 消息生产 实现
 */
@Slf4j
@Service
public class ProjectModuleProducerImpl implements ProjectModuleProducer {
    @Autowired
    private ProjectModuleSink projectModuleSink;

    /**
     * 创建 ProjectModule
     *
     * @param projectModule 账户 的实体类
     */
    @Override
    public void build(ProjectModule projectModule) {
        String linkId = RandomStringUtils.random(32, true, true);
        ProjectModuleMessage projectModuleMessage = new ProjectModuleMessage();
        BeanUtils.copyProperties(projectModule, projectModuleMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(projectModuleMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, projectModuleMessage.getCode());
        projectModuleSink.buildProjectModuleOutput().send(messageBuilder.build());
    }
}
