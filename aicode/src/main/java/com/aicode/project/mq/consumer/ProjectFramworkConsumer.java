package com.aicode.project.mq.consumer;

import com.aicode.project.entity.ProjectFramwork;
import com.aicode.project.message.ProjectFramworkMessage;
import com.aicode.project.mq.ProjectFramworkSink;
import com.aicode.project.service.ProjectFramworkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 项目应用技术 消费入口
 */
@Slf4j
@Service
public class ProjectFramworkConsumer {

    @Autowired
    private ProjectFramworkService projectFramworkService;

    /**
     * 监听 创建 project 数据消费
     *
     * @param projectFramworkMessage 玩家彩票id集合
     */
    @StreamListener(ProjectFramworkSink.buildProjectFramworkInput)
    public void autoReduceOddsInput(@Payload ProjectFramworkMessage projectFramworkMessage) {
        log.info("{}", projectFramworkMessage);
        try {
            ProjectFramwork projectFramwork = new ProjectFramwork();
            BeanUtils.copyProperties(projectFramworkMessage, projectFramwork);
            projectFramworkService.save(projectFramwork);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", projectFramworkMessage, e.getMessage());
        }

    }

}
