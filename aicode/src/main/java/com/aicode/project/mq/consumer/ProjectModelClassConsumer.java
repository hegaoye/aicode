package com.aicode.project.mq.consumer;

import com.aicode.project.entity.ProjectModelClass;
import com.aicode.project.message.ProjectModelClassMessage;
import com.aicode.project.mq.ProjectModelClassSink;
import com.aicode.project.service.ProjectModelClassService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 模块下的类 消费入口
 */
@Slf4j
@Service
public class ProjectModelClassConsumer {

    @Autowired
    private ProjectModelClassService projectModelClassService;

    /**
     * 监听 创建 project 数据消费
     *
     * @param projectModelClassMessage 玩家彩票id集合
     */
    @StreamListener(ProjectModelClassSink.buildProjectModelClassInput)
    public void autoReduceOddsInput(@Payload ProjectModelClassMessage projectModelClassMessage) {
        log.info("{}", projectModelClassMessage);
        try {
            ProjectModelClass projectModelClass = new ProjectModelClass();
            BeanUtils.copyProperties(projectModelClassMessage, projectModelClass);
            projectModelClassService.save(projectModelClass);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", projectModelClassMessage, e.getMessage());
        }

    }

}
