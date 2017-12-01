/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.FrameworkAttributeValueDAO;
import com.rzhkj.project.entity.FrameworkAttributeValue;
import com.rzhkj.project.service.FrameworkAttributeValueSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class FrameworkAttributeValueSVImpl extends BaseMybatisSVImpl<FrameworkAttributeValue, Long> implements FrameworkAttributeValueSV {

    @Resource
    private FrameworkAttributeValueDAO frameworkAttributeValueDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return frameworkAttributeValueDAO;
    }

}
