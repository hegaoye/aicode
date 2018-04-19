/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Maps;
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
import java.util.List;
import java.util.Map;


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
     * 批量存储 项目框架选择
     * 1.判断集合
     * 2.判断是否已经选择
     * 3.保存
     *
     * @param projectFramwors 项目选择的框架集合
     */
    @Override
    public void save(List<ProjectFramwork> projectFramwors) {
        //1.判断集合
        if (projectFramwors == null || projectFramwors.isEmpty()) {
            logger.warn(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new ProjectException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        projectFramwors.forEach(projectFramwork -> {
            //2.判断是否已经选择
            Map<String, Object> map = Maps.newHashMap();
            map.put("frameworkCode", projectFramwork.getFrameworkCode());
            map.put("projectCode", projectFramwork.getProjectCode());
            ProjectFramwork projectFramworkLoad = projectFramworkDAO.load(map);
            if (projectFramworkLoad == null) {
                //3.保存
                projectFramworkDAO.insert(projectFramwork);
            }
        });

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
