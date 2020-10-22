package com.aicode.map.mq.consumer;

import com.aicode.map.entity.MapRelationship;
import com.aicode.map.message.MapRelationshipMessage;
import com.aicode.map.mq.MapRelationshipSink;
import com.aicode.map.service.MapRelationshipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 模型关系 消费入口
 */
@Slf4j
@Service
public class MapRelationshipConsumer {

    @Autowired
    private MapRelationshipService mapRelationshipService;

    /**
     * 监听 创建 map 数据消费
     *
     * @param mapRelationshipMessage 玩家彩票id集合
     */
    @StreamListener(MapRelationshipSink.buildMapRelationshipInput)
    public void autoReduceOddsInput(@Payload MapRelationshipMessage mapRelationshipMessage) {
        log.info("{}", mapRelationshipMessage);
        try {
            MapRelationship mapRelationship = new MapRelationship();
            BeanUtils.copyProperties(mapRelationshipMessage, mapRelationship);
            mapRelationshipService.save(mapRelationship);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", mapRelationshipMessage, e.getMessage());
        }

    }

}
