package com.aicode.project.mq.consumer;

import com.aicode.project.entity.ProjectMap;
import com.aicode.project.message.ProjectMapMessage;
import com.aicode.project.mq.ProjectMapSink;
import com.aicode.project.service.ProjectMapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 项目数据表 消费入口
 */
@Slf4j
@Service
public class ProjectMapConsumer {

    @Autowired
    private ProjectMapService projectMapService;

    /**
     * 监听 创建 project 数据消费
     *
     * @param projectMapMessage 玩家彩票id集合
     */
    @StreamListener(ProjectMapSink.buildProjectMapInput)
    public void autoReduceOddsInput(@Payload ProjectMapMessage projectMapMessage) {
        log.info("{}", projectMapMessage);
        try {
            ProjectMap projectMap = new ProjectMap();
            BeanUtils.copyProperties(projectMapMessage, projectMap);
            projectMapService.save(projectMap);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", projectMapMessage, e.getMessage());
        }

    }

}
