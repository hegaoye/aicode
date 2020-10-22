package com.aicode.setting.mq;

import com.aicode.setting.entity.Setting;

/**
 * 消息生产 接口
 */
public interface SettingProducer {

    /**
     * 创建 Setting
     *
     * @param setting 设置 的实体类
     */
    void build(Setting setting);

}
