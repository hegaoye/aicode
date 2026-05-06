/*
 * demo
 */
package com.aicode.map.service;

import com.aicode.map.entity.MapRelationship;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 模型关系
 *
 * @author aicode
 */
public interface MapRelationshipService extends IService<MapRelationship> {

    int countByProjectCode(String code);

}


