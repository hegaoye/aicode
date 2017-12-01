/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ToolsCategoryDAO;
import com.rzhkj.project.entity.ToolsCategory;
import com.rzhkj.project.service.ToolsCategorySV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ToolsCategorySVImpl extends BaseMybatisSVImpl<ToolsCategory, Long> implements ToolsCategorySV {

    @Resource
    private ToolsCategoryDAO toolsCategoryDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return toolsCategoryDAO;
    }

}
