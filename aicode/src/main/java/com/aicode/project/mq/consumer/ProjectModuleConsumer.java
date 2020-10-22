package com.aicode.project.mq.consumer;

import com.aicode.project.entity.ProjectModule;
import com.aicode.project.message.ProjectModuleMessage;
import com.aicode.project.mq.ProjectModuleSink;
import com.aicode.project.service.ProjectModuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 项目选择模块 消费入口
 */
@Slf4j
@Service
public class ProjectModuleConsumer {

    @Autowired
    private ProjectModuleService projectModuleService;

    /**
     * 监听 创建 project 数据消费
     *
     * @param projectModuleMessage 玩家彩票id集合
     */
    @StreamListener(ProjectModuleSink.buildProjectModuleInput)
    public void autoReduceOddsInput(@Payload ProjectModuleMessage projectModuleMessage) {
        log.info("{}", projectModuleMessage);
        try {
            ProjectModule projectModule = new ProjectModule();
            BeanUtils.copyProperties(projectModuleMessage, projectModule);
            projectModuleService.save(projectModule);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", projectModuleMessage, e.getMessage());
        }

    }

}
