/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ProjectDAO;
import com.rzhkj.project.entity.Project;
import com.rzhkj.project.service.ProjectSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectSVImpl extends BaseMybatisSVImpl<Project, Long> implements ProjectSV {

    @Resource
    private ProjectDAO projectDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectDAO;
    }

}
