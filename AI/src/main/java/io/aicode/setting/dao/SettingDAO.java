/*
 *
 *                        http://www.aicode.io
 *
 *
 *        本代码仅用于AI-Code.
 *
 */


package io.aicode.setting.dao;

import io.aicode.core.base.BaseMybatisDAOImpl;
import io.aicode.setting.entity.Setting;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.stereotype.Repository;


@Repository
public class SettingDAO extends BaseMybatisDAOImpl<Setting, Long> {

    //根据key加载一个设置对象
    public Setting loadByKey(String k) {
        logger.info("====> 根据key加载一个设置对象 [Setting loadByKey(String k)]");
        logger.info("====> " + k);
        Setting setting = this.getSqlSession().selectOne(sqlmapNamespace + ".loadByKey", k);
        logger.info("<==== " + ToStringBuilder.reflectionToString(setting, ToStringStyle.MULTI_LINE_STYLE));
        return setting;
    }

    //修改设置值
    public void modifySetting(Setting setting) {
        logger.info("====> 修改设置值 [void modifySetting(Setting setting)]");
        logger.info("====> " + ToStringBuilder.reflectionToString(setting, ToStringStyle.MULTI_LINE_STYLE));
        this.getSqlSession().update(sqlmapNamespace + ".modifySetting", setting);
    }
}
