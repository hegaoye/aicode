/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ProjectMoudlesDAO;
import com.rzhkj.project.entity.ProjectMoudles;
import com.rzhkj.project.service.ProjectMoudlesSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectMoudlesSVImpl extends BaseMybatisSVImpl<ProjectMoudles, Long> implements ProjectMoudlesSV {

    @Resource
    private ProjectMoudlesDAO projectMoudlesDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectMoudlesDAO;
    }

}
