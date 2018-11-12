/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import io.aicode.core.base.BaseMybatisDAO;
import io.aicode.core.base.BaseMybatisSVImpl;
import io.aicode.project.dao.ProjectJobLogsDAO;
import io.aicode.project.entity.ProjectJobLogs;
import io.aicode.project.service.ProjectJobLogsSV;
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
