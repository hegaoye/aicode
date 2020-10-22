package com.aicode.project.mq.consumer;

import com.aicode.project.entity.ProjectJob;
import com.aicode.project.message.ProjectJobMessage;
import com.aicode.project.mq.ProjectJobSink;
import com.aicode.project.service.ProjectJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 任务 消费入口
 */
@Slf4j
@Service
public class ProjectJobConsumer {

    @Autowired
    private ProjectJobService projectJobService;

    /**
     * 监听 创建 project 数据消费
     *
     * @param projectJobMessage 玩家彩票id集合
     */
    @StreamListener(ProjectJobSink.buildProjectJobInput)
    public void autoReduceOddsInput(@Payload ProjectJobMessage projectJobMessage) {
        log.info("{}", projectJobMessage);
        try {
            ProjectJob projectJob = new ProjectJob();
            BeanUtils.copyProperties(projectJobMessage, projectJob);
            projectJobService.save(projectJob);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", projectJobMessage, e.getMessage());
        }

    }

}
