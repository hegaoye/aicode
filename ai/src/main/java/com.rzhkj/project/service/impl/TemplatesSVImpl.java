/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.TemplatesDAO;
import com.rzhkj.project.entity.Templates;
import com.rzhkj.project.service.TemplatesSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class TemplatesSVImpl extends BaseMybatisSVImpl<Templates, Long> implements TemplatesSV {

    @Resource
    private TemplatesDAO templatesDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return templatesDAO;
    }

}
