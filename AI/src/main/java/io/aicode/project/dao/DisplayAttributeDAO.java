/*
 *  * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于AI-Code.
 */

package io.aicode.project.dao;

import io.aicode.core.base.BaseMybatisDAOImpl;
import io.aicode.project.entity.DisplayAttribute;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * 显示属性
 * @author berton
 */
@Repository
public class DisplayAttributeDAO extends BaseMybatisDAOImpl<DisplayAttribute,Long>{

    /**
     * 加载一个对象显示属性 通过mapFieldColumnCode
     * @param mapFieldColumnCode 字段编码
     * @return DisplayAttribute
     */
    public DisplayAttribute loadByMapFieldColumnCode(String mapFieldColumnCode) {
        return getSqlSession().selectOne(this.sqlmapNamespace+".loadByMapFieldColumnCode",mapFieldColumnCode);
    }

    /**
     * 更新对象显示属性 通过mapFieldColumnCode
     * @param mapFieldColumnCode 字段编码
     */
    public void updateByMapFieldColumnCode(DisplayAttribute displayAttribute,String mapFieldColumnCode) {
        if(mapFieldColumnCode!=null){
           displayAttribute.setMapFieldColumnCode(mapFieldColumnCode);
        }
        getSqlSession().update(this.sqlmapNamespace+".update",displayAttribute);
    }

    /**
     * 删除对象显示属性
     * @param id @param mapFieldColumnCode 字段编码
     */
    public void delete(Long id,String mapFieldColumnCode) {
        Map<String,Object> map= new HashMap<String,Object>();
        if(id!=null){
           map.put("id",id);
        }
        if(mapFieldColumnCode!=null){
           map.put("mapFieldColumnCode",mapFieldColumnCode);
        }
        getSqlSession().delete(this.sqlmapNamespace+".delete",map);
   }
}
