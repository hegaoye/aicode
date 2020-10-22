package com.aicode.project.mq.consumer;

import com.aicode.project.entity.Project;
import com.aicode.project.message.ProjectMessage;
import com.aicode.project.mq.ProjectSink;
import com.aicode.project.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 项目信息 消费入口
 */
@Slf4j
@Service
public class ProjectConsumer {

    @Autowired
    private ProjectService projectService;

    /**
     * 监听 创建 project 数据消费
     *
     * @param projectMessage 玩家彩票id集合
     */
    @StreamListener(ProjectSink.buildProjectInput)
    public void autoReduceOddsInput(@Payload ProjectMessage projectMessage) {
        log.info("{}", projectMessage);
        try {
            Project project = new Project();
            BeanUtils.copyProperties(projectMessage, project);
            projectService.save(project);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", projectMessage, e.getMessage());
        }

    }

}
