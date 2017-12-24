/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;

import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ProjectModuleFrameworkDAO;
import com.rzhkj.project.entity.ProjectModuleFramework;
import com.rzhkj.project.service.ProjectModuleFrameworkSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectModuleFrameworkSVImpl extends BaseMybatisSVImpl<ProjectModuleFramework, Long> implements ProjectModuleFrameworkSV {

    @Resource
    private ProjectModuleFrameworkDAO projectModuleFrameworkDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectModuleFrameworkDAO;
    }

}
