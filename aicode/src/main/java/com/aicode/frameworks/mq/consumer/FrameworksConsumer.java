package com.aicode.frameworks.mq.consumer;

import com.aicode.frameworks.entity.Frameworks;
import com.aicode.frameworks.message.FrameworksMessage;
import com.aicode.frameworks.mq.FrameworksSink;
import com.aicode.frameworks.service.FrameworksService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 框架技术池 消费入口
 */
@Slf4j
@Service
public class FrameworksConsumer {

    @Autowired
    private FrameworksService frameworksService;

    /**
     * 监听 创建 frameworks 数据消费
     *
     * @param frameworksMessage 玩家彩票id集合
     */
    @StreamListener(FrameworksSink.buildFrameworksInput)
    public void autoReduceOddsInput(@Payload FrameworksMessage frameworksMessage) {
        log.info("{}", frameworksMessage);
        try {
            Frameworks frameworks = new Frameworks();
            BeanUtils.copyProperties(frameworksMessage, frameworks);
            frameworksService.save(frameworks);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", frameworksMessage, e.getMessage());
        }

    }

}
