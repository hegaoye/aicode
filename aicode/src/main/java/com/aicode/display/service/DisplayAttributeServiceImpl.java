/*
 * aicode
 */
package com.aicode.display.service;

import com.aicode.display.dao.DisplayAttributeDAO;
import com.aicode.display.dao.mapper.DisplayAttributeMapper;
import com.aicode.display.entity.DisplayAttribute;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 显示属性
 *
 * @author aicode
 */
@Slf4j
@Service
public class DisplayAttributeServiceImpl extends ServiceImpl<DisplayAttributeMapper, DisplayAttribute> implements DisplayAttributeService {

    @Autowired
    private DisplayAttributeDAO displayAttributeDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Transactional
    @Override
    public boolean save(DisplayAttribute entity) {
        entity.setId(uidGenerator.getUID());
        return super.save(entity);
    }

    @Override
    public int countByProjectCode(String code) {
        return displayAttributeDAO.countByProjectCode(code);
    }

}


