package com.aicode.display.mq.consumer;

import com.aicode.display.entity.DisplayAttribute;
import com.aicode.display.message.DisplayAttributeMessage;
import com.aicode.display.mq.DisplayAttributeSink;
import com.aicode.display.service.DisplayAttributeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 显示属性 消费入口
 */
@Slf4j
@Service
public class DisplayAttributeConsumer {

    @Autowired
    private DisplayAttributeService displayAttributeService;

    /**
     * 监听 创建 display 数据消费
     *
     * @param displayAttributeMessage 玩家彩票id集合
     */
    @StreamListener(DisplayAttributeSink.buildDisplayAttributeInput)
    public void autoReduceOddsInput(@Payload DisplayAttributeMessage displayAttributeMessage) {
        log.info("{}", displayAttributeMessage);
        try {
            DisplayAttribute displayAttribute = new DisplayAttribute();
            BeanUtils.copyProperties(displayAttributeMessage, displayAttribute);
            displayAttributeService.save(displayAttribute);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", displayAttributeMessage, e.getMessage());
        }

    }

}
