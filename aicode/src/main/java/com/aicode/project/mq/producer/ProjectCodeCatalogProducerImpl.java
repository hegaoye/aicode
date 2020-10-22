package com.aicode.project.mq.producer;

import com.aicode.project.entity.ProjectCodeCatalog;
import com.aicode.project.message.ProjectCodeCatalogMessage;
import com.aicode.project.mq.ProjectCodeCatalogProducer;
import com.aicode.project.mq.ProjectCodeCatalogSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 生成源码资料 消息生产 实现
 */
@Slf4j
@Service
public class ProjectCodeCatalogProducerImpl implements ProjectCodeCatalogProducer {
    @Autowired
    private ProjectCodeCatalogSink projectCodeCatalogSink;

    /**
     * 创建 ProjectCodeCatalog
     *
     * @param projectCodeCatalog 账户 的实体类
     */
    @Override
    public void build(ProjectCodeCatalog projectCodeCatalog) {
        String linkId = RandomStringUtils.random(32, true, true);
        ProjectCodeCatalogMessage projectCodeCatalogMessage = new ProjectCodeCatalogMessage();
        BeanUtils.copyProperties(projectCodeCatalog, projectCodeCatalogMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(projectCodeCatalogMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, projectCodeCatalogMessage.getCode());
        projectCodeCatalogSink.buildProjectCodeCatalogOutput().send(messageBuilder.build());
    }
}
