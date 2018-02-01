/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于龐帝業務系统.
 *
 */


package ${basePackage}.setting.dao;

import ${basePackage}.core.base.BaseMybatisDAOImpl;
import ${basePackage}.setting.entity.Setting;
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
