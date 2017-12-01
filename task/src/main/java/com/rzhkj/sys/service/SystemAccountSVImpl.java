/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于龐帝業務系统.
 *
 */

/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.sys.service;

import com.rzhkj.sys.dao.SystemAccountDAO;
import com.rzhkj.sys.entity.SystemAccount;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import com.baidu.fsg.uid.UidGenerator;
import org.springframework.stereotype.Service;


@Component
@Service
public class SystemAccountSVImpl extends BaseMybatisSVImpl<SystemAccount, Long> implements SystemAccountSV {

    @Resource
    private SystemAccountDAO systemAccountDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return systemAccountDAO;
    }

}
