package com.aicode.map.mq;

import com.aicode.map.entity.MapClassTable;

/**
 * 消息生产 接口
 */
public interface MapClassTableProducer {

    /**
     * 创建 MapClassTable
     *
     * @param mapClassTable 类表映射信息 的实体类
     */
    void build(MapClassTable mapClassTable);

}
