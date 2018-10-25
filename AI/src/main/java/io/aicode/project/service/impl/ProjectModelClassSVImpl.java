/*
 * http://www.aicode.io
本代码仅用于AI-Code.
 */
package io.aicode.project.service.impl;

import io.aicode.core.base.BaseMybatisDAO;
import io.aicode.core.base.BaseMybatisSVImpl;
import io.aicode.project.service.ProjectModelClassSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;


import io.aicode.project.dao.ProjectModelClassDAO;
import io.aicode.project.entity.ProjectModelClass;


/**
 * 模块下的类
 * @author lixin
 */
@Component
@Service
public class ProjectModelClassSVImpl extends BaseMybatisSVImpl<ProjectModelClass,Long> implements ProjectModelClassSV {


	@Resource
	private ProjectModelClassDAO projectModelClassDAO;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO(){
		return projectModelClassDAO;
	}


	/**
	 * 加载对象模块下的类 通过mapClassTableCode
	 * @param mapClassTableCode 类编码
	 * @return ProjectModelClass
	 */
     @Override
     public ProjectModelClass loadByMapClassTableCode(String mapClassTableCode) {
		return projectModelClassDAO.loadByMapClassTableCode(mapClassTableCode);
	 }
	/**
	 * 加载对象模块下的类 通过projectModelCode
	 * @param projectModelCode 模块编码
	 * @return ProjectModelClass
	 */
     @Override
     public ProjectModelClass loadByProjectModelCode(String projectModelCode) {
		return projectModelClassDAO.loadByProjectModelCode(projectModelCode);
	 }

     /**
      * 删除对象模块下的类
	   * @param id * @param mapClassTableCode 类编码* @param projectModelCode 模块编码
      */
	 @Override
     public void delete(Long id,String mapClassTableCode,String projectModelCode) {
		projectModelClassDAO.delete(id,mapClassTableCode,projectModelCode);
	 }



}