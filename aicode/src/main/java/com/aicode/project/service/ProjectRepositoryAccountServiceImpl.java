/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.service;

import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aicode.project.dao.ProjectRepositoryAccountDAO;
import com.aicode.project.dao.mapper.ProjectRepositoryAccountMapper;
import com.aicode.project.entity.ProjectRepositoryAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;


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

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(ProjectRepositoryAccount entity) {
//        entity.setId(String.valueOf(uidGenerator.getUID()));
        return super.save(entity);
    }

    /**
     * 分页查询 投注项水位
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<ProjectRepositoryAccount>
     */
    @Override
    public List<ProjectRepositoryAccount> list(QueryWrapper<ProjectRepositoryAccount> queryWrapper, int offset, int limit) {
        return projectRepositoryAccountDAO.list(queryWrapper, offset, limit);
    }
}


