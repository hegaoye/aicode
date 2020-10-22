package com.aicode.module.mq.consumer;

import com.aicode.module.entity.Module;
import com.aicode.module.message.ModuleMessage;
import com.aicode.module.mq.ModuleSink;
import com.aicode.module.service.ModuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 第三方模块池 消费入口
 */
@Slf4j
@Service
public class ModuleConsumer {

    @Autowired
    private ModuleService moduleService;

    /**
     * 监听 创建 module 数据消费
     *
     * @param moduleMessage 玩家彩票id集合
     */
    @StreamListener(ModuleSink.buildModuleInput)
    public void autoReduceOddsInput(@Payload ModuleMessage moduleMessage) {
        log.info("{}", moduleMessage);
        try {
            Module module = new Module();
            BeanUtils.copyProperties(moduleMessage, module);
            moduleService.save(module);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", moduleMessage, e.getMessage());
        }

    }

}
