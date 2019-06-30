/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import io.aicode.base.BaseMybatisDAO;
import io.aicode.base.BaseMybatisSVImpl;
import io.aicode.base.exceptions.BaseException;
import io.aicode.base.exceptions.ProjectException;
import io.aicode.base.tools.StringTools;
import io.aicode.project.dao.ProjectFramworkDAO;
import io.aicode.project.entity.ProjectFramwork;
import io.aicode.project.service.ProjectFramworkSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


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
     * 2.清空项目关联框架
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
        //2.清空项目关联框架
        projectFramworkDAO.deleteAll(projectFramwors.get(0).getProjectCode());
        //3.保存
        projectFramworkDAO.batchInsert(projectFramwors);
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
