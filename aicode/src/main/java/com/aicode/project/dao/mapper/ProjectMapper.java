package com.aicode.project.dao.mapper;

import com.aicode.project.entity.Project;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * Project mapper
 * 直接与xml映射
 *
 * @author hegaoye
 */
@Repository
public interface ProjectMapper extends BaseMapper<Project> {

}
