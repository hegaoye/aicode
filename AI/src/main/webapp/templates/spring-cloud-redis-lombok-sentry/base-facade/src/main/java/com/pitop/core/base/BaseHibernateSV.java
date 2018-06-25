package ${basePackage}.core.base;

import ${basePackage}.core.entity.Page;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * hibernate基础接口声明
 * Created by shangze on 2017/6/1.
 */
public interface BaseHibernateSV<E, PK extends Serializable> {

    /**
     * 插入对象merge
     *
     * @param obj 实体类对象
     */
    @Transactional(readOnly = true)
    void merge(E obj);

    /**
     * 根据id返回对象
     *
     * @param clazz 实体类对象
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    E queryById(Class<E> clazz, Long id);

    /**
     * 根据id返回对象
     * xx_like 模糊查询xx字段
     * xx_gt   大于查询xx字段
     * xx_lt   小于查询xx字段
     * order 排序标识
     * group 分组标识
     * limit 条数标识
     *
     * @param paras 实体类对象
     * @return
     */
    @Transactional(readOnly = true)
    E query(Class<E> clazz, Map<String, Object> paras);

    /**
     * 查询对象列表
     *
     * @param paras 实体类对象
     * @return
     */
    @Transactional(readOnly = true)
    List<E> queryList(Class<E> clazz, Map<String, Object> paras);

    /**
     * 查询count对象
     * xx_like 模糊查询xx字段
     * xx_gt   大于查询xx字段
     * xx_lt   小于查询xx字段
     * sql    拼接的原装sql
     * order 排序标识
     * group 分组标识
     * limit 条数标识
     *
     * @param paras 实体类对象
     * @return
     */
    @Transactional(readOnly = true)
    int count(Class<E> clazz, Map<String, Object> paras);

    /**
     * 查询sum数值
     * xx_like 模糊查询xx字段
     * xx_gt   大于查询xx字段
     * xx_lt   小于查询xx字段
     * order 排序标识
     * group 分组标识
     * limit 条数标识
     *
     * @param paras 实体类对象
     * @return
     */
    @Transactional(readOnly = true)
    Long sum(Class<E> clazz, Map<String, Object> paras, String sumfield);

    /**
     * Hql查询
     *
     * @param hql
     * @param objects
     * @return
     */
    @Transactional(readOnly = true)
    List<E> queryByHql(String hql, Object[] objects);

    /**
     * 分页查询
     *
     * @param clazz
     * @param page
     * @return
     */
    @Transactional(readOnly = true)
    Page queryPage(Class<E> clazz, Page page);

    /**
     * 分页查询Hql
     *
     * @param hql
     * @param pageNo   当前页码
     * @param pageSize 每页显示条数
     * @param objects  参数
     * @return
     */
    @Transactional(readOnly = true)
    Page queryPageByHql(String hql, Integer pageNo,
                        Integer pageSize, Object[] objects);

    /**
     * Sql查询
     *
     * @param sql
     * @return
     */
    @Transactional(readOnly = true)
    List queryBySql(String sql);

    /**
     * 分页查询Sql
     *
     * @param sql
     * @param pageNo   当前页码
     * @param pageSize 每页显示条数
     * @return
     */
    @Transactional(readOnly = true)
    Page queryPageBySql(String sql, Integer pageNo, Integer pageSize);

    /**
     * 执行HQL(删除,修改)
     *
     * @param hql
     * @param objects 参数
     * @return
     */
    @Transactional(readOnly = true)
    boolean executeHql(String hql, Object[] objects);

    /**
     * 执行SQL(删除,修改)
     *
     * @param sql
     * @return
     */
    @Transactional(readOnly = true)
    boolean executeSql(String sql);
}
