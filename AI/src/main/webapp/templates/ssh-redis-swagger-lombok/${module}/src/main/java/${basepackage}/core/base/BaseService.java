package ${basePackage}.core.base;
import java.util.List;
import java.util.Map;


/**
 * @version 1.0.0
 *          2016年3月17日14:18:20
 */
public interface BaseService<T> {
    /**
     * 插入对象merge
     *
     * @param obj 实体类对象
     */
    public void merge(T obj);

    /**
     * 插入对象save
     *
     * @param obj 实体类对象
     */
    public void save(T obj);

    public void saveOrUpdate(T obj);

    /**
     * 修改对象
     *
     * @param obj 实体类对象
     */
    public void update(T obj);

    /**
     * 根据id删除对象
     *
     * @param clazz 实体类对象
     * @param id
     */
    public void deleteById(Class<T> clazz, Integer id);

    /**
     * 删除对象
     *
     * @param obj 实体类对象
     */
    public void delete(T obj);
//
//    /**
//     * 删除对象列表
//     *
//     * @param list 实体类对象列表
//     */
//    public void deleteAll(List<T> list);

    /**
     * 根据id返回对象
     *
     * @param clazz 实体类对象
     * @param id
     * @return
     */
    public T queryById(Class<T> clazz, Integer id);

    /**
     * 查询对象
     * @param t 实体类对象
     * @return
     */
    public T query(Class<T> clazz,T t);

    /**
     * 查询对象列表
     * @param t 实体类对象
     * @return
     */
    public  List<T> queryList(Class<T> clazz,T t);
    /**
     *  查询count对象
     * @param t 实体类对象
     * @return
     */
    public int count(Class<T> clazz,T t);

    /**
     * 根据id返回对象
     * xx_like 模糊查询xx字段
     * xx_gt   大于查询xx字段
     * xx_lt   小于查询xx字段
     * order 排序标识
     * group 分组标识
     * limit 条数标识
     * @param paras 实体类对象
     * @return
     */
    public T query(Class<T> clazz,Map<String,Object> paras);

    /**
     * 查询对象列表
     * @param paras 实体类对象
     * @return
     */
    public  List<T> queryList(Class<T> clazz,Map<String,Object> paras);
    /**
     *  查询count对象
     * xx_like 模糊查询xx字段
     * xx_gt   大于查询xx字段
     * xx_lt   小于查询xx字段
     * sql    拼接的原装sql
     * order 排序标识
     * group 分组标识
     * limit 条数标识
     * @param paras 实体类对象
     * @return
     */
    public int count(Class<T> clazz,Map<String,Object> paras);
    /**
     * 查询sum数值
     * xx_like 模糊查询xx字段
     * xx_gt   大于查询xx字段
     * xx_lt   小于查询xx字段
     * order 排序标识
     * group 分组标识
     * limit 条数标识
     * @param paras 实体类对象
     * @return
     */
    public Long sum(Class<T> clazz,Map<String,Object> paras,String sumfield);
    /**
     * Hql查询
     *
     * @param hql
     * @param objects
     * @return
     */
    public List<Map> queryByHql(String hql, Object[] objects) throws Exception;

    /**
     * 分页查询
     * @param clazz
     * @param page
     * @return
     */
    public Page queryPage(Class<T> clazz, Page page);
    /**
     * 分页查询Hql
     *
     * @param hql
     * @param pageNo   当前页码
     * @param pageSize 每页显示条数
     * @param objects  参数
     * @return
     */
    public Page queryPageByHql(String hql, Integer pageNo,
                                Integer pageSize, Object[] objects);

    /**
     * Sql查询
     *
     * @param sql
     * @return
     */
    public List queryBySql(String sql);

    /**
     * 分页查询Sql
     *
     * @param sql
     * @param pageNo   当前页码
     * @param pageSize 每页显示条数
     * @return
     */
    public Page queryPageBySql(String sql, Integer pageNo, Integer pageSize);

    /**
     * 执行HQL(删除,修改)
     *
     * @param hql
     * @param objects 参数
     * @return
     */
    public boolean executeHql(String hql, Object[] objects);

    /**
     * 执行SQL(删除,修改)
     *
     * @param sql
     * @return
     */
    public boolean executeSql(String sql);

}
