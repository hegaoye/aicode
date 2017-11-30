/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ProjectJobLogsDAO;
import com.rzhkj.project.entity.ProjectJobLogs;
import com.rzhkj.project.service.ProjectJobLogsSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectJobLogsSVImpl extends BaseMybatisSVImpl<ProjectJobLogs, Long> implements ProjectJobLogsSV {

    @Resource
    private ProjectJobLogsDAO projectJobLogsDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectJobLogsDAO;
    }

}
