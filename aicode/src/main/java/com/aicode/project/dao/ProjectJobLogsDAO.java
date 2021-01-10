package com.aicode.project.dao;

import com.aicode.project.dao.mapper.ProjectJobLogsMapper;
import com.aicode.project.entity.ProjectJobLogs;
import com.aicode.core.base.BaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * ProjectJobLogs DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class ProjectJobLogsDAO extends BaseDAO<ProjectJobLogsMapper, ProjectJobLogs> {


    /**
     * ProjectJobLogs mapper
     */
    @Autowired
    private ProjectJobLogsMapper projectJobLogsMapper;


}