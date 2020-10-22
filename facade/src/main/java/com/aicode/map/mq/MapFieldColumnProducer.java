package com.aicode.map.mq;

import com.aicode.map.entity.MapFieldColumn;

/**
 * 消息生产 接口
 */
public interface MapFieldColumnProducer {

    /**
     * 创建 MapFieldColumn
     *
     * @param mapFieldColumn 字段属性映射信息 的实体类
     */
    void build(MapFieldColumn mapFieldColumn);

}
