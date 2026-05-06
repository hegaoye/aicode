/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.setting.service;

import com.aicode.setting.dao.SettingDAO;
import com.aicode.setting.dao.mapper.SettingMapper;
import com.aicode.setting.entity.Setting;
import com.aicode.setting.entity.SettingKey;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 设置
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class SettingServiceImpl extends ServiceImpl<SettingMapper, Setting> implements SettingService {

    @Autowired
    private SettingDAO settingDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(Setting entity) {
        entity.setId(uidGenerator.getUID());
        return super.save(entity);
    }


    /**
     * 根据key 获得 value
     *
     * @param key Setting.Key
     * @return T
     */
    @Override
    public <T> T load(SettingKey key, Class<T>... clazz) {
        Setting setting = this.getOne(new LambdaQueryWrapper<Setting>().eq(Setting::getK, key.name()));
        if (clazz == null || clazz.length <= 0 || clazz[0].equals(String.class)) {
            T t = toStr(setting.getV(), (Class<T>) String.class);
            log.info(" setting 获取 key : " + t.toString());
            return t;
        }
        if (clazz[0].equals(Integer.class)) {
            T t = clazz[0].cast(Integer.parseInt(setting.getV()));
            log.info(" setting 获取 key : " + t.toString());
            return t;
        }
        return null;
    }

    private <T> T toStr(String value, Class<T> clazz) {
        return clazz.cast((String.valueOf(value)));
    }
}


