/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ProjectTablesDAO;
import com.rzhkj.project.entity.ProjectTables;
import com.rzhkj.project.service.ProjectTablesSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectTablesSVImpl extends BaseMybatisSVImpl<ProjectTables, Long> implements ProjectTablesSV {

    @Resource
    private ProjectTablesDAO projectTablesDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectTablesDAO;
    }

}
