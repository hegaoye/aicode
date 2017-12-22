/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;

import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ProjectServiceModuleClassDAO;
import com.rzhkj.project.entity.ProjectServiceModuleClass;
import com.rzhkj.project.service.ProjectServiceModuleClassSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectServiceModuleClassSVImpl extends BaseMybatisSVImpl<ProjectServiceModuleClass, Long> implements ProjectServiceModuleClassSV {

    @Resource
    private ProjectServiceModuleClassDAO projectServiceModuleClassDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectServiceModuleClassDAO;
    }

}
