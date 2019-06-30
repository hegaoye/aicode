/*
 * http://www.aicode.io
本代码仅用于AI-Code.
 */
package io.aicode.display.facade;

import io.aicode.base.BaseMybatisSV;
import io.aicode.display.entity.DisplayAttribute;

import java.util.List;

/**
 * 显示属性
 *
 * @author lixin
 */
public interface DisplayAttributeSV extends BaseMybatisSV<DisplayAttribute, Long> {

    /**
     * 加载对象显示属性 通过mapFieldColumnCode
     *
     * @param mapFieldColumnCode 字段编码
     * @return DisplayAttribute
     */
    DisplayAttribute loadByMapFieldColumnCode(String mapFieldColumnCode);


    /**
     * 删除对象显示属性
     *
     * @param id @param mapFieldColumnCode 字段编码
     * @return DisplayAttribute
     */
    void delete(Long id, String mapFieldColumnCode);


    /**
     * 添加或修改
     * @param displayAttributes  显示属性
     */
    void saveOrUpdate(List<DisplayAttribute> displayAttributes);


    /**
     * 根据项目编码统计是否已经设置显示属性
     * @param projectCode    项目编码
     * @return int
     */
    int countByProjectCode(String projectCode);


}
