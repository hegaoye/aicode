package com.aicode.display.dao;

import com.aicode.display.dao.mapper.DisplayAttributeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * DisplayAttribute DAO
 * 数据服务层
 *
 * @author aicode
 */
@Repository
public class DisplayAttributeDAO {


    /**
     * DisplayAttribute mapper
     */
    @Autowired
    private DisplayAttributeMapper displayAttributeMapper;

    public int countByProjectCode(String code) {
        return displayAttributeMapper.countByProjectCode(code);
    }
}