/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.FrameworkAttributeDAO;
import com.rzhkj.project.entity.FrameworkAttribute;
import com.rzhkj.project.service.FrameworkAttributeSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class FrameworkAttributeSVImpl extends BaseMybatisSVImpl<FrameworkAttribute, Long> implements FrameworkAttributeSV {

    @Resource
    private FrameworkAttributeDAO frameworkAttributeDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return frameworkAttributeDAO;
    }

}
