/*
 * http://www.aicode.io
本代码仅用于AI-Code.
 */
package io.aicode.display.service;

import io.aicode.core.base.BaseMybatisDAO;
import io.aicode.core.base.BaseMybatisSVImpl;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


import io.aicode.display.facade.DisplayAttributeSV;
import io.aicode.display.dao.DisplayAttributeDAO;
import io.aicode.display.entity.DisplayAttribute;


/**
 * 显示属性
 *
 * @author lixin
 */
@Component
@Service
public class DisplayAttributeSVImpl extends BaseMybatisSVImpl<DisplayAttribute, Long> implements DisplayAttributeSV {


    @Resource
    private DisplayAttributeDAO displayAttributeDAO;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return displayAttributeDAO;
    }


    /**
     * 加载对象显示属性 通过mapFieldColumnCode
     *
     * @param mapFieldColumnCode 字段编码
     * @return DisplayAttribute
     */
    @Override
    public DisplayAttribute loadByMapFieldColumnCode(String mapFieldColumnCode) {
        return displayAttributeDAO.loadByMapFieldColumnCode(mapFieldColumnCode);
    }

    /**
     * 删除对象显示属性
     *
     * @param id * @param mapFieldColumnCode 字段编码
     */
    @Override
    public void delete(Long id, String mapFieldColumnCode) {
        displayAttributeDAO.delete(id, mapFieldColumnCode);
    }


}