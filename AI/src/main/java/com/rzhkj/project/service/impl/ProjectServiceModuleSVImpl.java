/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;

import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ProjectServiceModuleDAO;
import com.rzhkj.project.entity.ProjectServiceModule;
import com.rzhkj.project.service.ProjectServiceModuleSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectServiceModuleSVImpl extends BaseMybatisSVImpl<ProjectServiceModule, Long> implements ProjectServiceModuleSV {

    @Resource
    private ProjectServiceModuleDAO projectServiceModuleDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectServiceModuleDAO;
    }

}
