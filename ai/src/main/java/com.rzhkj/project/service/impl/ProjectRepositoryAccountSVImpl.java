/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ProjectRepositoryAccountDAO;
import com.rzhkj.project.entity.ProjectRepositoryAccount;
import com.rzhkj.project.service.ProjectRepositoryAccountSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectRepositoryAccountSVImpl extends BaseMybatisSVImpl<ProjectRepositoryAccount, Long> implements ProjectRepositoryAccountSV {

    @Resource
    private ProjectRepositoryAccountDAO projectRepositoryAccountDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectRepositoryAccountDAO;
    }

}
