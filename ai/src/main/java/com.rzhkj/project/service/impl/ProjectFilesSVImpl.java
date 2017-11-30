/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ProjectFilesDAO;
import com.rzhkj.project.entity.ProjectFiles;
import com.rzhkj.project.service.ProjectFilesSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ProjectFilesSVImpl extends BaseMybatisSVImpl<ProjectFiles, Long> implements ProjectFilesSV {

    @Resource
    private ProjectFilesDAO projectFilesDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return projectFilesDAO;
    }

}
