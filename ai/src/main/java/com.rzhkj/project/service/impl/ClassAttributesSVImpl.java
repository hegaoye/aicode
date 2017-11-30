/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ClassAttributesDAO;
import com.rzhkj.project.entity.ClassAttributes;
import com.rzhkj.project.service.ClassAttributesSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ClassAttributesSVImpl extends BaseMybatisSVImpl<ClassAttributes, Long> implements ClassAttributesSV {

    @Resource
    private ClassAttributesDAO classAttributesDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return classAttributesDAO;
    }

}
