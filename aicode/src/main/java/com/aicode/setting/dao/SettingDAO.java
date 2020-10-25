package com.aicode.setting.dao;

import com.aicode.setting.dao.mapper.SettingMapper;
import com.aicode.setting.entity.Setting;
import com.aicode.core.base.BaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Setting DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class SettingDAO extends BaseDAO<SettingMapper, Setting> {


    /**
     * Setting mapper
     */
    @Autowired
    private SettingMapper settingMapper;


}