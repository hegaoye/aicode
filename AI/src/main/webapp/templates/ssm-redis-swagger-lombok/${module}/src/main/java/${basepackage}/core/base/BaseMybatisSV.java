package ${basePackage}.core.base;

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

    /**
     * 检查字段是否存在
     *
     * @param property    例如 phone
     * @param propertyVal 例如 13174108520
     * @return 存在返回true ;不存在返回false
     * @author bobai 更新于2017.07.03
     */
    @Transactional(readOnly = true)
    boolean isExist(String property, String propertyVal) throws BaseException;
    /**
     * 根据条件集合 检查字段是否存在
     *
     * @param property      例如 phone 这项要和实体类对应，否则不起效
     * @param propertyVal   例如 13174108520
     * @param conditionsMap 条件集合
     * @return 存在返回true ;不存在返回false
     */
    @Transactional(readOnly = true)
    boolean isExistByConditions(String property, String propertyVal, Map conditionsMap) throws BaseException;

}
