/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.SettingDAO;
import com.rzhkj.project.entity.Setting;
import com.rzhkj.project.service.SettingSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class SettingSVImpl extends BaseMybatisSVImpl<Setting, Long> implements SettingSV {

    @Resource
    private SettingDAO settingDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return settingDAO;
    }

}
