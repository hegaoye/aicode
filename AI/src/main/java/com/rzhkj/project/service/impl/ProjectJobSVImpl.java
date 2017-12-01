/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ProjectJobDAO;
import com.rzhkj.project.entity.ProjectJob;
import com.rzhkj.project.service.ProjectJobSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectJobSVImpl extends BaseMybatisSVImpl<ProjectJob, Long> implements ProjectJobSV {

    @Resource
    private ProjectJobDAO projectJobDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectJobDAO;
    }

}
