/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package io.aicode.project.service.impl;

import com.baidu.fsg.uid.UidGenerator;
import io.aicode.base.BaseMybatisDAO;
import io.aicode.base.BaseMybatisSVImpl;
import io.aicode.project.dao.ProjectModuleDAO;
import io.aicode.project.entity.ProjectModule;
import io.aicode.project.service.ProjectModuleSV;
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
