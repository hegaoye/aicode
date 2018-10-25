/*
 *  *
 *                       http://www.aicode.io
 *
 *
 *      本代码仅用于AI-Code.
 */
package io.aicode.project.service;

import io.aicode.core.base.BaseMybatisSV;
import io.aicode.project.entity.DisplayAttribute;

/**
 * 显示属性
 *
 * @author berton
 */
public interface DisplayAttributeSV extends BaseMybatisSV<DisplayAttribute,Long>{

    /**
     * 加载对象显示属性 通过mapFieldColumnCode
     * @param mapFieldColumnCode 字段编码
     * @return DisplayAttribute
     */
     DisplayAttribute loadByMapFieldColumnCode(String mapFieldColumnCode);


    /**
     * 删除对象显示属性
     * @param id @param mapFieldColumnCode 字段编码
     * @return DisplayAttribute
     */
     void delete(Long id, String mapFieldColumnCode);



}
