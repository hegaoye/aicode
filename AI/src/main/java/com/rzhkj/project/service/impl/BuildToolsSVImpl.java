/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;

import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.BuildToolsDAO;
import com.rzhkj.project.entity.BuildTools;
import com.rzhkj.project.service.BuildToolsSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class BuildToolsSVImpl extends BaseMybatisSVImpl<BuildTools, Long> implements BuildToolsSV {

    @Resource
    private BuildToolsDAO buildToolsDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return buildToolsDAO;
    }

}