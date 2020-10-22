package com.aicode.project.mq.consumer;

import com.aicode.project.entity.ProjectModel;
import com.aicode.project.message.ProjectModelMessage;
import com.aicode.project.mq.ProjectModelSink;
import com.aicode.project.service.ProjectModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 模块 消费入口
 */
@Slf4j
@Service
public class ProjectModelConsumer {

    @Autowired
    private ProjectModelService projectModelService;

    /**
     * 监听 创建 project 数据消费
     *
     * @param projectModelMessage 玩家彩票id集合
     */
    @StreamListener(ProjectModelSink.buildProjectModelInput)
    public void autoReduceOddsInput(@Payload ProjectModelMessage projectModelMessage) {
        log.info("{}", projectModelMessage);
        try {
            ProjectModel projectModel = new ProjectModel();
            BeanUtils.copyProperties(projectModelMessage, projectModel);
            projectModelService.save(projectModel);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", projectModelMessage, e.getMessage());
        }

    }

}
