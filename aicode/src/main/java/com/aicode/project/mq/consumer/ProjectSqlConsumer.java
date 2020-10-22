package com.aicode.project.mq.consumer;

import com.aicode.project.entity.ProjectSql;
import com.aicode.project.message.ProjectSqlMessage;
import com.aicode.project.mq.ProjectSqlSink;
import com.aicode.project.service.ProjectSqlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 项目sql脚本 消费入口
 */
@Slf4j
@Service
public class ProjectSqlConsumer {

    @Autowired
    private ProjectSqlService projectSqlService;

    /**
     * 监听 创建 project 数据消费
     *
     * @param projectSqlMessage 玩家彩票id集合
     */
    @StreamListener(ProjectSqlSink.buildProjectSqlInput)
    public void autoReduceOddsInput(@Payload ProjectSqlMessage projectSqlMessage) {
        log.info("{}", projectSqlMessage);
        try {
            ProjectSql projectSql = new ProjectSql();
            BeanUtils.copyProperties(projectSqlMessage, projectSql);
            projectSqlService.save(projectSql);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", projectSqlMessage, e.getMessage());
        }

    }

}
