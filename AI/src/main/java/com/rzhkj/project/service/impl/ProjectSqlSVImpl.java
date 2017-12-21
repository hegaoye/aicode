/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;

import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ProjectSqlDAO;
import com.rzhkj.project.entity.ProjectSql;
import com.rzhkj.project.service.ProjectSqlSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectSqlSVImpl extends BaseMybatisSVImpl<ProjectSql, Long> implements ProjectSqlSV {

    @Resource
    private ProjectSqlDAO projectSqlDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectSqlDAO;
    }

}
