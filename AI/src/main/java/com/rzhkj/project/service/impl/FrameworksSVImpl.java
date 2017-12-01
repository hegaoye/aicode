/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.FrameworksDAO;
import com.rzhkj.project.entity.Frameworks;
import com.rzhkj.project.service.FrameworksSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class FrameworksSVImpl extends BaseMybatisSVImpl<Frameworks, Long> implements FrameworksSV {

    @Resource
    private FrameworksDAO frameworksDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return frameworksDAO;
    }

}
