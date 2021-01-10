/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.map.service;

import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aicode.map.dao.MapFieldColumnDAO;
import com.aicode.map.dao.mapper.MapFieldColumnMapper;
import com.aicode.map.entity.MapFieldColumn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;


/**
 * 字段属性映射信息
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class MapFieldColumnServiceImpl extends ServiceImpl<MapFieldColumnMapper, MapFieldColumn> implements MapFieldColumnService {

    @Autowired
    private MapFieldColumnDAO mapFieldColumnDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(MapFieldColumn entity) {
//        entity.setId(String.valueOf(uidGenerator.getUID()));
        return super.save(entity);
    }

    /**
     * 分页查询 投注项水位
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<MapFieldColumn>
     */
    @Override
    public List<MapFieldColumn> list(QueryWrapper<MapFieldColumn> queryWrapper, int offset, int limit) {
        return mapFieldColumnDAO.list(queryWrapper, offset, limit);
    }
}


