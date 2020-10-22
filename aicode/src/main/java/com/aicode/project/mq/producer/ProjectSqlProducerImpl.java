package com.aicode.project.mq.producer;

import com.aicode.project.entity.ProjectSql;
import com.aicode.project.message.ProjectSqlMessage;
import com.aicode.project.mq.ProjectSqlProducer;
import com.aicode.project.mq.ProjectSqlSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 项目sql脚本 消息生产 实现
 */
@Slf4j
@Service
public class ProjectSqlProducerImpl implements ProjectSqlProducer {
    @Autowired
    private ProjectSqlSink projectSqlSink;

    /**
     * 创建 ProjectSql
     *
     * @param projectSql 账户 的实体类
     */
    @Override
    public void build(ProjectSql projectSql) {
        String linkId = RandomStringUtils.random(32, true, true);
        ProjectSqlMessage projectSqlMessage = new ProjectSqlMessage();
        BeanUtils.copyProperties(projectSql, projectSqlMessage);

        MessageBuilder messageBuilder = MessageBuilder.withPayload(projectSqlMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
//                .setHeader(RocketMQHeaders.TAGS, projectSqlMessage.getCode());
        projectSqlSink.buildProjectSqlOutput().send(messageBuilder.build());
    }
}
