package com.aicode.database.dao.mapper;

import com.aicode.database.entity.Column;
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
public interface ColumMapper extends BaseMapper<Column> {

    List<Column> list(@Param("database") String database,@Param("tableName") String tableName);

}
