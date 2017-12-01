/*
 * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于龐帝業務系统.
 */

/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.jobgui.dao;


import com.rzhkj.core.base.BaseMybatisDAOImpl;
import com.rzhkj.jobgui.entity.QrtzCalendars;
import org.springframework.stereotype.Repository;

/**
 * 存放日历信息
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
@Repository
public class QrtzCalendarsDao extends BaseMybatisDAOImpl<QrtzCalendars, String> {

    public String getIbatisMapperNamesapce() {
        return "QrtzCalendars";
    }

    public void saveOrUpdate(QrtzCalendars entity) {
        if (entity.getSchedName() == null)
            insert(entity);
        else
            update(entity);
    }

}
