/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.service;

import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aicode.map.dao.MapClassTableDAO;
import com.aicode.map.dao.mapper.MapClassTableMapper;
import com.aicode.map.entity.MapClassTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;


/**
 * 类表映射信息
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class MapClassTableServiceImpl extends ServiceImpl<MapClassTableMapper, MapClassTable> implements MapClassTableService {

    @Autowired
    private MapClassTableDAO mapClassTableDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(MapClassTable entity) {
//        entity.setId(String.valueOf(uidGenerator.getUID()));
        return super.save(entity);
    }

    /**
     * 分页查询 投注项水位
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<MapClassTable>
     */
    @Override
    public List<MapClassTable> list(QueryWrapper<MapClassTable> queryWrapper, int offset, int limit) {
        return mapClassTableDAO.list(queryWrapper, offset, limit);
    }
}


