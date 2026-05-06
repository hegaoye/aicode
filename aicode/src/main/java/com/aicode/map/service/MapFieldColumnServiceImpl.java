/*
 * aicode
 */
package com.aicode.map.service;

import com.aicode.map.dao.MapFieldColumnDAO;
import com.aicode.map.dao.mapper.MapFieldColumnMapper;
import com.aicode.map.entity.MapFieldColumn;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 字段属性映射信息
 *
 * @author aicode
 */
@Slf4j
@Service
public class MapFieldColumnServiceImpl extends ServiceImpl<MapFieldColumnMapper, MapFieldColumn> implements MapFieldColumnService {

    @Autowired
    private MapFieldColumnDAO mapFieldColumnDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Transactional
    @Override
    public boolean save(MapFieldColumn entity) {
        entity.setId(uidGenerator.getUID());
        return super.save(entity);
    }


}


