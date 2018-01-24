package ${basePackage}.core.base;

import ${basePackage}.core.entity.Page;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 基础接口声明
 */
public interface BaseMybatisSV<E, PK extends Serializable> {

    /**
     * 数据保存
     *
     * @param entity 实体
     * @
     */
    void save(E entity);


    /**
     * 数据更新
     *
     * @param entity 实体
     * @
     */
    void update(E entity);


    /**
     * 根据id查询实体
     *
     * @param id
     * @return
     * @
     */
    E load(PK id);


    /**
     * 根据id查询实体
     *
     * @param param
     * @return
     * @
     */
    E load(Map<String, Object> param);

    /**
     * 查询所有数据
     *
     * @return
     * @
     */
    List<E> getAll();

    /**
     * 分页查询
     *
     * @param page 分页对象
     * @return List<E>
     * @
     */
    Page<E> getList(Page<E> page);


    /**
     * @param map
     * @return
     * @author 立坤 更新于2017.07.21
     * @remark 根据条件查询
     * @
     */
    List<E> queryList(Map<String, Object> map);


    /**
     * 根据id判断是插入还是更新
     *
     * @param entity 实体
     * @
     */
    void saveOrUpdate(E entity);


    /**
     * 根据id进行删除数据
     *
     * @param id 对象id
     * @
     */
    void deleteById(PK id);

    /**
     * @param ids
     * @author 立坤 更新于2017.07.21
     * @remark 批量删除信息
     * @
     */
    void deleteByIds(List<Long> ids);

    /**
     * 判断是否唯一
     *
     * @param entity              实体
     * @param uniquePropertyNames 判断属性名称
     * @return boolean
     * @
     */

    boolean isUnique(E entity, String uniquePropertyNames);
}
