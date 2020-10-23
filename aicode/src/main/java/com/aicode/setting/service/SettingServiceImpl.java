/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.setting.service;

import com.aicode.setting.entity.SettingKey;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aicode.setting.dao.SettingDAO;
import com.aicode.setting.dao.mapper.SettingMapper;
import com.aicode.setting.entity.Setting;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;
import java.util.Map;


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
//        entity.setId(String.valueOf(uidGenerator.getUID()));
        return super.save(entity);
    }

    /**
     * 分页查询 投注项水位
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<Setting>
     */
    @Override
    public List<Setting> list(QueryWrapper<Setting> queryWrapper, int offset, int limit) {
        return settingDAO.list(queryWrapper, offset, limit);
    }

    /**
     * 根据key 获得 value
     *
     * @param key Setting.Key
     * @return T
     */
    @Override
    public <T> T load(SettingKey key, Class<T>... clazz) {
        Setting setting = settingDAO.selectOne(new LambdaQueryWrapper<Setting>().eq(Setting::getK, key.name()));
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


