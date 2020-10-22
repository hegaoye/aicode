package com.aicode.display.dao;

import com.aicode.display.dao.mapper.DisplayAttributeMapper;
import com.aicode.display.entity.DisplayAttribute;
import com.aicode.core.base.BaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * DisplayAttribute DAO
 * 数据服务层
 *
 * @author hegaoye
 */
@Repository
public class DisplayAttributeDAO extends BaseDAO<DisplayAttributeMapper, DisplayAttribute> {


    /**
     * DisplayAttribute mapper
     */
    @Autowired
    private DisplayAttributeMapper displayAttributeMapper;


}