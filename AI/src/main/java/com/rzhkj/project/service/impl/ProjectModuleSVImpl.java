/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;

import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ProjectModuleDAO;
import com.rzhkj.project.entity.ProjectModule;
import com.rzhkj.project.service.ProjectModuleSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectModuleSVImpl extends BaseMybatisSVImpl<ProjectModule, Long> implements ProjectModuleSV {

    @Resource
    private ProjectModuleDAO projectModuleDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectModuleDAO;
    }

}
