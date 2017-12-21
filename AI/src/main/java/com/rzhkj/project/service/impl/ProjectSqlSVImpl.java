/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;

import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.exceptions.ProjectException;
import com.rzhkj.core.tools.StringTools;
import com.rzhkj.project.dao.ProjectSqlDAO;
import com.rzhkj.project.entity.ProjectSql;
import com.rzhkj.project.entity.ProjectSqlStateEnum;
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

    @Override
    public void save(ProjectSql entity) throws BaseException {
        if (entity == null || StringTools.isEmpty(entity.getProjectCode()) || StringTools.isEmpty(entity.getTsql())) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        entity.setCode(String.valueOf(uidGenerator.getUID()));
        entity.setState(ProjectSqlStateEnum.Enable.name());
        super.save(entity);
    }

    /**
     * 删除项目sql
     *
     * @param code tsql编码
     */
    @Override
    public void delete(String code) {
        if (StringTools.isEmpty(code)) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        projectSqlDAO.delete(code);
    }
}
