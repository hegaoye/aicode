package ${basePackage}.core.base;


import ${basePackage}.core.entity.Page;

import java.util.List;
import java.util.Map;


/**
 *
 * @version 1.0.0
 * 2017年3月18日10:33:19
 */ 

public interface BaseHibernateDAO<T> {
	
	/**
	 * 插入对象merge
	 * @param obj
	 */
	public void merge(T obj);
	
	/**
	 * 插入对象save
	 * @param obj
	 */
	public void save(T obj);

	/**
	 * 修改对象
	 * @param obj
	 */
	public void update(T obj);

	/**
	 * 根据ID删除对象
	 * @param id
	 */
	public void deleteById(Class<T> obj, Long id);
	
	/**
	 * 删除对象
	 * @param obj
	 */
	public void delete(T obj);

	/**
	 * 删除对象列表
	 * @return
	 */
	public void deleteAll(List<T> list);
	
	/**
	 * 根据id返回对象
	 * @param id
	 * @return
	 */
	public T selectById(Class<T> clazz, Long id);

	/**
	 * 根据paras返回对象
	 * @return
	 */
	public T select(Class<T> clazz, Map<String, Object> paras);


	/**
	 * 根据id返回对象
	 *
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
	public List<T> selectAll(Class<T> clazz, Map<String, Object> paras);
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
	public int count(Class<T> clazz, Map<String, Object> paras);
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
	public Long sum(Class<T> clazz, Map<String, Object> paras, String sumfield);
	/**
	 * Hql查询
	 * @param hql
	 * @param objects
	 * @return
	 */
	public List<T> selectByHql(String hql, Object[] objects);

	/**
	 * 分页查询
	 * @param clazz
	 * @param page
     * @return
     */
	public Page selectPage(Class<T> clazz, Page page);
	/** 
     * 分页查询Hql
     * @param hql 
     * @param pageNo 页码
     * @param pageSize 每页k显示条数
     * @param objects 
     * @return 
     */  
	public Page selectPageByHql(String hql, Integer pageNo, Integer pageSize, Object[] objects);

	/**
	 * Sql查询
	 * @param sql
	 * @return
	 */
	public List selectBySql(String sql);
    /** 
     * 分页查询Sql
     * @param sql 
     * @param pageNo 页码
     * @param pageSize 每页显示条数
     * @return 
     */ 
	public Page selectPageBySql(String sql, Integer pageNo, Integer pageSize);
    
    /** 
     * 执行HQL(删除,修改)
     * @param hql 
     * @param objects 
     * @return 
     */  
	public boolean executeHql(String hql, Object[] objects);  
    
	
    /** 
     * 执行SQL(删除,修改)
     * @param sql 
     * @return 
     */  
	public boolean executeSql(String sql);


	
}
