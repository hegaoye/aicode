package com.aicode.map.mq.consumer;

import com.aicode.map.entity.MapClassTable;
import com.aicode.map.message.MapClassTableMessage;
import com.aicode.map.mq.MapClassTableSink;
import com.aicode.map.service.MapClassTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * 类表映射信息 消费入口
 */
@Slf4j
@Service
public class MapClassTableConsumer {

    @Autowired
    private MapClassTableService mapClassTableService;

    /**
     * 监听 创建 map 数据消费
     *
     * @param mapClassTableMessage 玩家彩票id集合
     */
    @StreamListener(MapClassTableSink.buildMapClassTableInput)
    public void autoReduceOddsInput(@Payload MapClassTableMessage mapClassTableMessage) {
        log.info("{}", mapClassTableMessage);
        try {
            MapClassTable mapClassTable = new MapClassTable();
            BeanUtils.copyProperties(mapClassTableMessage, mapClassTable);
            mapClassTableService.save(mapClassTable);
        } catch (Exception e) {
            log.error("自动降赔通知玩家彩票id集合异常-{}-异常信息-{}", mapClassTableMessage, e.getMessage());
        }

    }

}
