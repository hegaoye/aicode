package com.aicode.setting.mq.consumer;

import com.aicode.setting.entity.Setting;
import com.aicode.setting.message.SettingMessage;
import com.aicode.setting.mq.SettingSink;
import com.aicode.setting.service.SettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 设置 消费入口
 */
@Slf4j
@Service
public class SettingConsumer {

    @Autowired
    private SettingService settingService;

    /**
     * 监听 创建 setting 数据消费
     *
     * @param settingMessage 玩家彩票id集合
     */
    @StreamListener(SettingSink.buildSettingInput)
    public void autoReduceOddsInput(@Payload SettingMessage settingMessage) {
        log.info("{}", settingMessage);
        try {
            Setting setting = new Setting();
            BeanUtils.copyProperties(settingMessage, setting);
            settingService.save(setting);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", settingMessage, e.getMessage());
        }

    }

}
