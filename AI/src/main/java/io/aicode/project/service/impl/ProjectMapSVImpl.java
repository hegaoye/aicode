/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import io.aicode.core.base.BaseMybatisDAO;
import io.aicode.core.base.BaseMybatisSVImpl;
import io.aicode.project.dao.ProjectMapDAO;
import io.aicode.project.entity.ProjectMap;
import io.aicode.project.service.ProjectMapSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectMapSVImpl extends BaseMybatisSVImpl<ProjectMap, Long> implements ProjectMapSV {

    @Resource
    private ProjectMapDAO projectMapDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectMapDAO;
    }

}
