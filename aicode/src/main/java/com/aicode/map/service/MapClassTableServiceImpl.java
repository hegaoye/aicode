/*
 * aicode
 */
package com.aicode.map.service;

import com.aicode.map.dao.MapClassTableDAO;
import com.aicode.map.dao.mapper.MapClassTableMapper;
import com.aicode.map.entity.MapClassTable;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 类表映射信息
 *
 * @author aicode
 */
@Slf4j
@Service
public class MapClassTableServiceImpl extends ServiceImpl<MapClassTableMapper, MapClassTable> implements MapClassTableService {

    @Autowired
    private MapClassTableDAO mapClassTableDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Transactional
    @Override
    public boolean save(MapClassTable entity) {
        entity.setId(uidGenerator.getUID());
        return super.save(entity);
    }


}


