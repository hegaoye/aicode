/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;

import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.BuildToolsPathDAO;
import com.rzhkj.project.entity.BuildToolsPath;
import com.rzhkj.project.service.BuildToolsPathSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class BuildToolsPathSVImpl extends BaseMybatisSVImpl<BuildToolsPath, Long> implements BuildToolsPathSV {

    @Resource
    private BuildToolsPathDAO buildToolsPathDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return buildToolsPathDAO;
    }

}
