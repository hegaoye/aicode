/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import io.aicode.core.base.BaseMybatisDAO;
import io.aicode.core.base.BaseMybatisSVImpl;
import io.aicode.core.exceptions.BaseException;
import io.aicode.core.exceptions.ProjectRepositoryAccountException;
import io.aicode.core.tools.StringTools;
import io.aicode.project.dao.ProjectRepositoryAccountDAO;
import io.aicode.project.entity.ProjectRepositoryAccount;
import io.aicode.project.entity.ProjectRepositoryAccountStateEnum;
import io.aicode.project.entity.ProjectRepositoryTypeEnum;
import io.aicode.project.service.ProjectRepositoryAccountSV;
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
        projectRepositoryAccount.setType(projectRepositoryAccount.getType().equalsIgnoreCase(ProjectRepositoryTypeEnum.GIT.name()) ? ProjectRepositoryTypeEnum.GIT.name() : ProjectRepositoryTypeEnum.SVN.name());

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
