/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.exceptions.ProjectException;
import com.rzhkj.core.tools.StringTools;
import com.rzhkj.project.dao.ProjectToolsDAO;
import com.rzhkj.project.entity.ProjectTools;
import com.rzhkj.project.service.ProjectToolsSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectToolsSVImpl extends BaseMybatisSVImpl<ProjectTools, Long> implements ProjectToolsSV {

    @Resource
    private ProjectToolsDAO projectToolsDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectToolsDAO;
    }

    /**
     * 删除
     *
     * @param projectTools 项目工具对象
     */
    @Override
    public void delete(ProjectTools projectTools) {
        if (projectTools == null || StringTools.isEmpty(projectTools.getProjectCode()) || StringTools.isEmpty(projectTools.getToolsCode())) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        projectToolsDAO.delete(projectTools);
    }
}
