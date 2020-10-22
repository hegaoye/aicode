package com.aicode.module.mq.consumer;

import com.aicode.module.entity.ModuleFile;
import com.aicode.module.message.ModuleFileMessage;
import com.aicode.module.mq.ModuleFileSink;
import com.aicode.module.service.ModuleFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 模块文件信息 消费入口
 */
@Slf4j
@Service
public class ModuleFileConsumer {

    @Autowired
    private ModuleFileService moduleFileService;

    /**
     * 监听 创建 module 数据消费
     *
     * @param moduleFileMessage 玩家彩票id集合
     */
    @StreamListener(ModuleFileSink.buildModuleFileInput)
    public void autoReduceOddsInput(@Payload ModuleFileMessage moduleFileMessage) {
        log.info("{}", moduleFileMessage);
        try {
            ModuleFile moduleFile = new ModuleFile();
            BeanUtils.copyProperties(moduleFileMessage, moduleFile);
            moduleFileService.save(moduleFile);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", moduleFileMessage, e.getMessage());
        }

    }

}
