/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ProjectFramworkDAO;
import com.rzhkj.project.entity.ProjectFramwork;
import com.rzhkj.project.service.ProjectFramworkSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectFramworkSVImpl extends BaseMybatisSVImpl<ProjectFramwork, Long> implements ProjectFramworkSV {

    @Resource
    private ProjectFramworkDAO projectFramworkDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectFramworkDAO;
    }

}
