package com.aicode.project.mq.consumer;

import com.aicode.project.entity.ProjectJobLogs;
import com.aicode.project.message.ProjectJobLogsMessage;
import com.aicode.project.mq.ProjectJobLogsSink;
import com.aicode.project.service.ProjectJobLogsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 任务日志 消费入口
 */
@Slf4j
@Service
public class ProjectJobLogsConsumer {

    @Autowired
    private ProjectJobLogsService projectJobLogsService;

    /**
     * 监听 创建 project 数据消费
     *
     * @param projectJobLogsMessage 玩家彩票id集合
     */
    @StreamListener(ProjectJobLogsSink.buildProjectJobLogsInput)
    public void autoReduceOddsInput(@Payload ProjectJobLogsMessage projectJobLogsMessage) {
        log.info("{}", projectJobLogsMessage);
        try {
            ProjectJobLogs projectJobLogs = new ProjectJobLogs();
            BeanUtils.copyProperties(projectJobLogsMessage, projectJobLogs);
            projectJobLogsService.save(projectJobLogs);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", projectJobLogsMessage, e.getMessage());
        }

    }

}
