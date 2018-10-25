/*
 *  *
 *                       http://www.aicode.io
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
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
