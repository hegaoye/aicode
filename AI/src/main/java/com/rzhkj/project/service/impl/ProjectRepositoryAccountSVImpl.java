/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.exceptions.ProjectRepositoryAccountException;
import com.rzhkj.core.tools.StringTools;
import com.rzhkj.project.dao.ProjectRepositoryAccountDAO;
import com.rzhkj.project.entity.ProjectRepositoryAccount;
import com.rzhkj.project.entity.ProjectRepositoryAccountStateEnum;
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


    /**
     * 创建账户
     *
     * @param projectRepositoryAccount 实体
     * @throws BaseException
     */
    @Override
    public void save(ProjectRepositoryAccount projectRepositoryAccount) throws BaseException {
        if (projectRepositoryAccount == null
                || StringTools.isEmpty(projectRepositoryAccount.getAccount())
                || StringTools.isEmpty(projectRepositoryAccount.getDescription())
                || StringTools.isEmpty(projectRepositoryAccount.getHome())
                || StringTools.isEmpty(projectRepositoryAccount.getPassword())
                || StringTools.isEmpty(projectRepositoryAccount.getType())
                ) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectRepositoryAccountException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        projectRepositoryAccount.setCode(String.valueOf(uidGenerator.getUID()));
        projectRepositoryAccount.setState(ProjectRepositoryAccountStateEnum.Enable.name());

        super.save(projectRepositoryAccount);
    }


    /**
     * 删除
     *
     * @param code 版本管理编码
     */
    @Override
    public void delete(String code) {
        projectRepositoryAccountDAO.delete(code);
    }
}
