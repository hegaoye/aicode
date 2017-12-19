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
import com.rzhkj.project.dao.ProjectFramworkDAO;
import com.rzhkj.project.entity.ProjectFramwork;
import com.rzhkj.project.service.ProjectFramworkSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectFramworkSVImpl extends BaseMybatisSVImpl<ProjectFramwork, Long> implements ProjectFramworkSV {

    @Resource
    private ProjectFramworkDAO projectFramworkDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectFramworkDAO;
    }


    /**
     * 删除
     *
     * @param projectFramwork 项目技术框架对象
     */
    @Override
    public void delete(ProjectFramwork projectFramwork) {
        if (projectFramwork == null || StringTools.isEmpty(projectFramwork.getProjectCode()) || StringTools.isEmpty(projectFramwork.getFrameworkCode())) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        projectFramworkDAO.delete(projectFramwork);
    }
}
