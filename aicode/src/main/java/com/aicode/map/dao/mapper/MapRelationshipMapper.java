package com.aicode.map.dao.mapper;

import com.aicode.map.entity.MapRelationship;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * MapRelationship mapper
 * 直接与xml映射
 *
 * @author hegaoye
 */
@Repository
public interface MapRelationshipMapper extends BaseMapper<MapRelationship> {

    int countByProjectCode(@Param("projectCode") String projectCode);

}
