/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;

import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ProjectCodeCatalogDAO;
import com.rzhkj.project.entity.ProjectCodeCatalog;
import com.rzhkj.project.service.ProjectCodeCatalogSV;
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
