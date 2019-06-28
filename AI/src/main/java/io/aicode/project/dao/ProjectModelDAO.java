/*
 * http://www.aicode.io
本代码仅用于AI-Code.
 */

package io.aicode.project.dao;

import io.aicode.core.base.BaseMybatisDAOImpl;
import org.springframework.stereotype.Repository;
import io.aicode.project.entity.ProjectModel;

import java.util.HashMap;
import java.util.Map;

/**
 * 模块
 * @author lixin
 */
@Repository
public class ProjectModelDAO extends BaseMybatisDAOImpl<ProjectModel,Long> {

    /**
     * 加载一个对象模块 通过code
     * @param code 模块编码
     * @return ProjectModel
     */
    public ProjectModel loadByCode(String code) {
        return getSqlSession().selectOne(this.sqlmapNamespace+".loadByCode",code);
    }

    /**
     * 更新对象模块 通过code
     * @param code 模块编码
     */
    public void updateByCode(ProjectModel projectModel,String code) {
        if(code!=null){
           projectModel.setCode(code);
        }
        getSqlSession().update(this.sqlmapNamespace+".update",projectModel);
    }

    /**
     * 删除对象模块
     * @param id @param code 模块编码
     */
    public void delete(Long id,String code) {
        Map<String,Object> map= new HashMap<String,Object>();
        if(id!=null){
           map.put("id",id);
        }
        if(code!=null){
           map.put("code",code);
        }
        getSqlSession().delete(this.sqlmapNamespace+".delete",map);
   }
}
