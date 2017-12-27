/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ProjectMapDAO;
import com.rzhkj.project.entity.ProjectMap;
import com.rzhkj.project.service.ProjectMapSV;
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
