package com.aicode.project.mq.consumer;

import com.aicode.project.entity.ProjectCodeCatalog;
import com.aicode.project.message.ProjectCodeCatalogMessage;
import com.aicode.project.mq.ProjectCodeCatalogSink;
import com.aicode.project.service.ProjectCodeCatalogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 生成源码资料 消费入口
 */
@Slf4j
@Service
public class ProjectCodeCatalogConsumer {

    @Autowired
    private ProjectCodeCatalogService projectCodeCatalogService;

    /**
     * 监听 创建 project 数据消费
     *
     * @param projectCodeCatalogMessage 玩家彩票id集合
     */
    @StreamListener(ProjectCodeCatalogSink.buildProjectCodeCatalogInput)
    public void autoReduceOddsInput(@Payload ProjectCodeCatalogMessage projectCodeCatalogMessage) {
        log.info("{}", projectCodeCatalogMessage);
        try {
            ProjectCodeCatalog projectCodeCatalog = new ProjectCodeCatalog();
            BeanUtils.copyProperties(projectCodeCatalogMessage, projectCodeCatalog);
            projectCodeCatalogService.save(projectCodeCatalog);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", projectCodeCatalogMessage, e.getMessage());
        }

    }

}
