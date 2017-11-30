/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ProjectToolsDAO;
import com.rzhkj.project.entity.ProjectTools;
import com.rzhkj.project.service.ProjectToolsSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectToolsSVImpl extends BaseMybatisSVImpl<ProjectTools, Long> implements ProjectToolsSV {

    @Resource
    private ProjectToolsDAO projectToolsDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectToolsDAO;
    }

}
