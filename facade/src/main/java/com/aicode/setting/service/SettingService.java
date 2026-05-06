/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.setting.service;

import com.aicode.setting.entity.Setting;
import com.aicode.setting.entity.SettingKey;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 设置
 *
 * @author hegaoye
 */
public interface SettingService extends IService<Setting> {

    /**
     * 根据key 获得 value
     *
     * @param key Setting.Key
     * @return T
     */
    <T> T load(SettingKey key, Class<T>... clazz);
}


