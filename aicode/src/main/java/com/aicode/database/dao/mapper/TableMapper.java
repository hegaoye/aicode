package com.aicode.database.dao.mapper;

import com.aicode.database.entity.Table;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DisplayAttribute mapper
 * 直接与xml映射
 *
 * @author hegaoye
 */
@Repository
public interface TableMapper extends BaseMapper<Table> {

    Table load(@Param("database") String database, @Param("tableName") String tableName);

    long count(@Param("database") String database);

    List<Table> list(@Param("database") String database);

}
