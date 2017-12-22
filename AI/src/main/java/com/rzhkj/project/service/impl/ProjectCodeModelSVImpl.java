/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;

import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ProjectCodeModelDAO;
import com.rzhkj.project.entity.ProjectCodeModel;
import com.rzhkj.project.service.ProjectCodeModelSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectCodeModelSVImpl extends BaseMybatisSVImpl<ProjectCodeModel, Long> implements ProjectCodeModelSV {

    @Resource
    private ProjectCodeModelDAO projectCodeModelDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectCodeModelDAO;
    }

}
