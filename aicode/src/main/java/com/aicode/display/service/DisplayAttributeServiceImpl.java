/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.display.service;

import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aicode.display.dao.DisplayAttributeDAO;
import com.aicode.display.dao.mapper.DisplayAttributeMapper;
import com.aicode.display.entity.DisplayAttribute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;


/**
 * 显示属性
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class DisplayAttributeServiceImpl extends ServiceImpl<DisplayAttributeMapper, DisplayAttribute> implements DisplayAttributeService {

    @Autowired
    private DisplayAttributeDAO displayAttributeDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(DisplayAttribute entity) {
//        entity.setId(String.valueOf(uidGenerator.getUID()));
        return super.save(entity);
    }

    /**
     * 分页查询 投注项水位
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<DisplayAttribute>
     */
    @Override
    public List<DisplayAttribute> list(QueryWrapper<DisplayAttribute> queryWrapper, int offset, int limit) {
        return displayAttributeDAO.list(queryWrapper, offset, limit);
    }
}


