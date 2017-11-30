/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ProjectClassDAO;
import com.rzhkj.project.entity.ProjectClass;
import com.rzhkj.project.service.ProjectClassSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectClassSVImpl extends BaseMybatisSVImpl<ProjectClass, Long> implements ProjectClassSV {

    @Resource
    private ProjectClassDAO projectClassDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectClassDAO;
    }

}
