/*
 * aicode
 */
package com.aicode.map.service;

import com.aicode.map.dao.MapRelationshipDAO;
import com.aicode.map.dao.mapper.MapRelationshipMapper;
import com.aicode.map.entity.MapRelationship;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 模型关系
 *
 * @author aicode
 */
@Slf4j
@Service
public class MapRelationshipServiceImpl extends ServiceImpl<MapRelationshipMapper, MapRelationship> implements MapRelationshipService {

    @Autowired
    private MapRelationshipDAO mapRelationshipDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Transactional
    @Override
    public boolean save(MapRelationship entity) {
        entity.setId(uidGenerator.getUID());
        return super.save(entity);
    }


    @Override
    public int countByProjectCode(String code) {
        return mapRelationshipDAO.countByProjectCode(code);
    }

}


