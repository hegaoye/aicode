package com.aicode.display.dao.mapper;

import com.aicode.display.entity.DisplayAttribute;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * DisplayAttribute mapper
 * 直接与xml映射
 *
 * @author hegaoye
 */
@Repository
public interface DisplayAttributeMapper extends BaseMapper<DisplayAttribute> {

    int countByProjectCode(@Param("code") String code);
}
