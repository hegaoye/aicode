/*
 * http://www.aicode.io
本代码仅用于AI-Code.
 */

package io.aicode.project.dao;

import io.aicode.base.BaseMybatisDAOImpl;
import org.springframework.stereotype.Repository;
import io.aicode.project.entity.ProjectModelClass;

import java.util.HashMap;
import java.util.Map;

/**
 * 模块下的类
 *
 * @author lixin
 */
@Repository
public class ProjectModelClassDAO extends BaseMybatisDAOImpl<ProjectModelClass, Long> {

    /**
     * 加载一个对象模块下的类 通过mapClassTableCode
     *
     * @param mapClassTableCode 类编码
     * @return ProjectModelClass
     */
    public ProjectModelClass loadByMapClassTableCode(String mapClassTableCode) {
        return getSqlSession().selectOne(this.sqlmapNamespace + ".loadByMapClassTableCode", mapClassTableCode);
    }

    /**
     * 更新对象模块下的类 通过mapClassTableCode
     *
     * @param mapClassTableCode 类编码
     */
    public void updateByMapClassTableCode(ProjectModelClass projectModelClass, String mapClassTableCode) {
        if (mapClassTableCode != null) {
            projectModelClass.setMapClassTableCode(mapClassTableCode);
        }
        getSqlSession().update(this.sqlmapNamespace + ".update", projectModelClass);
    }


    /**
     * 加载一个对象模块下的类 通过projectModelCode
     *
     * @param projectModelCode 模块编码
     * @return ProjectModelClass
     */
    public ProjectModelClass loadByProjectModelCode(String projectModelCode) {
        return getSqlSession().selectOne(this.sqlmapNamespace + ".loadByProjectModelCode", projectModelCode);
    }

    /**
     * 更新对象模块下的类 通过projectModelCode
     *
     * @param projectModelCode 模块编码
     */
    public void updateByProjectModelCode(ProjectModelClass projectModelClass, String projectModelCode) {
        if (projectModelCode != null) {
            projectModelClass.setProjectModelCode(projectModelCode);
        }
        getSqlSession().update(this.sqlmapNamespace + ".update", projectModelClass);
    }

    /**
     * 删除对象模块下的类
     *
     * @param id @param mapClassTableCode 类编码@param projectModelCode 模块编码
     */
    public void delete(Long id, String mapClassTableCode, String projectModelCode) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (id != null) {
            map.put("id", id);
        }
        if (mapClassTableCode != null) {
            map.put("mapClassTableCode", mapClassTableCode);
        }
        if (projectModelCode != null) {
            map.put("projectModelCode", projectModelCode);
        }
        getSqlSession().delete(this.sqlmapNamespace + ".delete", map);
    }
}
