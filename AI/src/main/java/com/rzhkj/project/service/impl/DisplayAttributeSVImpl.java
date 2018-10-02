/*
 *  * Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于AI-Code.
 */
package com.rzhkj.project.service.impl;

import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.DisplayAttributeDAO;
import com.rzhkj.project.entity.DisplayAttribute;
import com.rzhkj.project.service.DisplayAttributeSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;



/**
 * 显示属性
 * @author berton
 */
@Component
@Service
public class DisplayAttributeSVImpl extends BaseMybatisSVImpl<DisplayAttribute,Long> implements DisplayAttributeSV {


	@Resource
	private DisplayAttributeDAO displayAttributeDAO;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO(){
		return displayAttributeDAO;
	}


	/**
	 * 加载对象显示属性 通过mapFieldColumnCode
	 * @param mapFieldColumnCode 字段编码
	 * @return DisplayAttribute
	 */
     @Override
     public DisplayAttribute loadByMapFieldColumnCode(String mapFieldColumnCode) {
		return displayAttributeDAO.loadByMapFieldColumnCode(mapFieldColumnCode);
	 }

     /**
      * 删除对象显示属性
	   * @param id * @param mapFieldColumnCode 字段编码
      */
	 @Override
     public void delete(Long id,String mapFieldColumnCode) {
		displayAttributeDAO.delete(id,mapFieldColumnCode);
	 }



}