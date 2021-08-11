package com.aicode.database.dao.mapper;

import com.aicode.database.entity.Database;
import com.aicode.database.entity.Table;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author hegaoye
 */
@Repository
public interface DatabaseMapper extends BaseMapper<Database> {


    long count(@Param("schemaName") String schemaName);

    void createDatabase(@Param("database") String database);

    void createTable(@Param("sql") String sql);

    void useDatabase(@Param("database") String database);

    void dropDatabase(@Param("database") String database);
}
