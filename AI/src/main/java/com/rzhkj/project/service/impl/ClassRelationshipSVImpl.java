/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ClassRelationshipDAO;
import com.rzhkj.project.entity.ClassRelationship;
import com.rzhkj.project.service.ClassRelationshipSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ClassRelationshipSVImpl extends BaseMybatisSVImpl<ClassRelationship, Long> implements ClassRelationshipSV {

    @Resource
    private ClassRelationshipDAO classRelationshipDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return classRelationshipDAO;
    }

}
