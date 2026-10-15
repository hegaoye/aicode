/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.aicode.core.BaseException;
import com.aicode.core.tools.PasswordCrypto;
import com.aicode.exceptions.ProjectRepositoryAccountException;
import com.aicode.project.dao.ProjectRepositoryAccountDAO;
import com.aicode.project.dao.mapper.ProjectRepositoryAccountMapper;
import com.aicode.project.entity.ProjectRepositoryAccount;
import com.aicode.project.entity.ProjectRepositoryAccountState;
import com.aicode.project.entity.ProjectRepositoryTypeEnum;
// 原为 com.alibaba.druid.util.StringUtils：与 commons-lang3 同名但语义不同，且随 Druid 消失
// 会导致编译失败。此处改用 commons-lang3，并按语义等价关系 isEmpty -> isBlank：
// druid 的 isEmpty 把「仅空白字符」也视作空，commons-lang3 的 isEmpty 不视作空。
// 改用 isBlank 才能保持原有校验强度（whitespace-only 的 projectCode 仍被判为空）。
import org.apache.commons.lang3.StringUtils;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.IdUtil;


/**
 * 版本控制管理
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class ProjectRepositoryAccountServiceImpl extends ServiceImpl<ProjectRepositoryAccountMapper, ProjectRepositoryAccount> implements ProjectRepositoryAccountService {

    @Autowired
    private ProjectRepositoryAccountDAO projectRepositoryAccountDAO;
    @Override
    public boolean save(ProjectRepositoryAccount projectRepositoryAccount) {
        if (projectRepositoryAccount == null
                || StringUtils.isBlank(projectRepositoryAccount.getAccount())
                || StringUtils.isBlank(projectRepositoryAccount.getDescription())
                || StringUtils.isBlank(projectRepositoryAccount.getHome())
                || StringUtils.isBlank(projectRepositoryAccount.getPassword())
                || StringUtils.isBlank(projectRepositoryAccount.getType())) {
            log.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectRepositoryAccountException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        projectRepositoryAccount.setCode(IdUtil.getSnowflakeNextIdStr());
        projectRepositoryAccount.setState(ProjectRepositoryAccountState.Enable.name());
        projectRepositoryAccount.setType(projectRepositoryAccount.getType()
                .equalsIgnoreCase(ProjectRepositoryTypeEnum.GIT.name()) ? ProjectRepositoryTypeEnum.GIT.name() : ProjectRepositoryTypeEnum.SVN.name());
        // 入库前加密产物仓库凭据
        projectRepositoryAccount.setPassword(PasswordCrypto.encrypt(projectRepositoryAccount.getPassword()));

        return super.save(projectRepositoryAccount);
    }
}


