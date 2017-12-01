/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ToolsDAO;
import com.rzhkj.project.entity.Tools;
import com.rzhkj.project.service.ToolsSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ToolsSVImpl extends BaseMybatisSVImpl<Tools, Long> implements ToolsSV {

    @Resource
    private ToolsDAO toolsDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return toolsDAO;
    }

}
