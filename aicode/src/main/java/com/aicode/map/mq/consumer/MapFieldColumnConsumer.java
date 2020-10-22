package com.aicode.map.mq.consumer;

import com.aicode.map.entity.MapFieldColumn;
import com.aicode.map.message.MapFieldColumnMessage;
import com.aicode.map.mq.MapFieldColumnSink;
import com.aicode.map.service.MapFieldColumnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 字段属性映射信息 消费入口
 */
@Slf4j
@Service
public class MapFieldColumnConsumer {

    @Autowired
    private MapFieldColumnService mapFieldColumnService;

    /**
     * 监听 创建 map 数据消费
     *
     * @param mapFieldColumnMessage 玩家彩票id集合
     */
    @StreamListener(MapFieldColumnSink.buildMapFieldColumnInput)
    public void autoReduceOddsInput(@Payload MapFieldColumnMessage mapFieldColumnMessage) {
        log.info("{}", mapFieldColumnMessage);
        try {
            MapFieldColumn mapFieldColumn = new MapFieldColumn();
            BeanUtils.copyProperties(mapFieldColumnMessage, mapFieldColumn);
            mapFieldColumnService.save(mapFieldColumn);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", mapFieldColumnMessage, e.getMessage());
        }

    }

}
