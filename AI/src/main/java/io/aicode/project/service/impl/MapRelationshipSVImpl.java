/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import io.aicode.core.base.BaseMybatisDAO;
import io.aicode.core.base.BaseMybatisSVImpl;
import io.aicode.project.dao.MapRelationshipDAO;
import io.aicode.project.entity.MapRelationship;
import io.aicode.project.service.MapRelationshipSV;
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
