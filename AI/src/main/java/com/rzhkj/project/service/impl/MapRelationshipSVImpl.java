/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.MapRelationshipDAO;
import com.rzhkj.project.entity.MapRelationship;
import com.rzhkj.project.service.MapRelationshipSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class MapRelationshipSVImpl extends BaseMybatisSVImpl<MapRelationship, Long> implements MapRelationshipSV {

    @Resource
    private MapRelationshipDAO mapRelationshipDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return mapRelationshipDAO;
    }

}
