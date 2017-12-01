/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ClassInfoDAO;
import com.rzhkj.project.entity.ClassInfo;
import com.rzhkj.project.service.ClassInfoSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ClassInfoSVImpl extends BaseMybatisSVImpl<ClassInfo, Long> implements ClassInfoSV {

    @Resource
    private ClassInfoDAO classInfoDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return classInfoDAO;
    }

}
