package com.aicode.map.dao.mapper;

import com.aicode.map.entity.MapFieldColumn;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MapFieldColumn mapper
 * 直接与xml映射
 *
 * @author hegaoye
 */
@Repository
public interface MapFieldColumnMapper extends BaseMapper<MapFieldColumn> {

    void batchInsert(List<MapFieldColumn> mapFieldColumns);
}
