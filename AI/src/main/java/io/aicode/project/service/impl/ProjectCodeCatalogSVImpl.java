/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package io.aicode.project.service.impl;

import com.baidu.fsg.uid.UidGenerator;
import io.aicode.base.BaseMybatisDAO;
import io.aicode.base.BaseMybatisSVImpl;
import io.aicode.project.dao.ProjectCodeCatalogDAO;
import io.aicode.project.entity.ProjectCodeCatalog;
import io.aicode.project.service.ProjectCodeCatalogSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectCodeCatalogSVImpl extends BaseMybatisSVImpl<ProjectCodeCatalog, Long> implements ProjectCodeCatalogSV {

    @Resource
    private ProjectCodeCatalogDAO projectCodeCatalogDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectCodeCatalogDAO;
    }

}
