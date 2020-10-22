package com.aicode.frameworks.mq.consumer;

import com.aicode.frameworks.entity.FrameworksTemplate;
import com.aicode.frameworks.message.FrameworksTemplateMessage;
import com.aicode.frameworks.mq.FrameworksTemplateSink;
import com.aicode.frameworks.service.FrameworksTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 框架配置文件模板 消费入口
 */
@Slf4j
@Service
public class FrameworksTemplateConsumer {

    @Autowired
    private FrameworksTemplateService frameworksTemplateService;

    /**
     * 监听 创建 frameworks 数据消费
     *
     * @param frameworksTemplateMessage 玩家彩票id集合
     */
    @StreamListener(FrameworksTemplateSink.buildFrameworksTemplateInput)
    public void autoReduceOddsInput(@Payload FrameworksTemplateMessage frameworksTemplateMessage) {
        log.info("{}", frameworksTemplateMessage);
        try {
            FrameworksTemplate frameworksTemplate = new FrameworksTemplate();
            BeanUtils.copyProperties(frameworksTemplateMessage, frameworksTemplate);
            frameworksTemplateService.save(frameworksTemplate);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", frameworksTemplateMessage, e.getMessage());
        }

    }

}
