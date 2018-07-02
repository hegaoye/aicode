package ${basePackage}.core.base;

import ${basePackage}.core.exceptions.BaseException;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 通用DAO接口. <p/>
 * 其中的泛型参数E为实体，是一个与表字段一一对应的JavaBean;<p/>
 * PK是主键类型，是一个实现序列化Serializable接口的类型，一般为Long型;<p/>
 * 通过继承此DAO可以享有此接口所拥有的操作方法，建议用户所有的DAO都继承此DAO。
 * 在此约定，所有实体的主键一律用id命名，并且类型对应java中long型。
 *
 * @author lixin 11-12-12 下午10:37
 */
public interface BaseMybatisDAO<E, PK extends Serializable> {
    /**
     * Sqlmap的ID常量，指定插入操作，值为insert
     */
    String SQLID_INSERT = "insert";
    /**
     * Sqlmap的ID常量，指定插入操作，值为batchInsert
     */
    String SQLID_BATCH_INSERT = "batchInsert";
    /**
     * Sqlmap的ID常量，指定更新操作，值为batchUpdate
     */
    String SQLID_BATCH_UPDATE = "batchUpdate";
    /**
     * Sqlmap的ID常量，指定更新操作，值为update
     */
    String SQLID_UPDATE = "update";

    /**
     * Sqlmap的ID常量，指定更新操作，值为updateBy
     */
    String SQLID_UPDATE_BY = "updateBy";
    /**
     * Sqlmap的ID常量，指定删除操作，值为delete
     */
    String SQLID_DELETE = "delete";
    /**
     * sqlmap的ID常量，批量删除 batchDelete
     */
    String SQLID_BATCH_DELETE = "batchDelete";
    /**
     * Sqlmap的ID常量，指定通过id加载一条操作，值为load
     */
    String SQLID_LOAD = "load";
    /**
     * Sqlmap的ID常量，指定复杂查询操作返回多条记录，值为query
     */
    String SQLID_QUERY = "query";
    /**
     * Sqlmap的ID常量，指定复杂查询操作的统计操作，值为count
     */
    String SQLID_COUNT = "count";
    /**
     * 接口常量，用于命名Sqlmap中的排序字段属性名，值为“sortColumn”
     */
    String ORDERY_BY_PARAM_NAMES = "sortColumns";
    /**
     * 接口常量，用于判断某个属性值是否为唯一，值为“isUnique”
     */
    String SQLID_ISUNIQUE = "isUnique";

    /**
     * 插入操作，将一个实体保存到数据库中，当这个实体的主键是自增或序列类型时候，会自动回填主键ID，并返回。<p/>
     * 反之，只保存实体，并返回该实体。
     *
     * @param entity 实体
     * @return 返回保存后的实体，此返回的实体通常带有ID。
     */
    E insert(E entity);

    /**
     * 更新操作，将一个实体更新到数据库对应ID的记录上，此更新字段与SQLMap配置的字段有关。
     *
     * @param entity 实体
     * @return 返回更细的实体
     */
    E update(E entity);

    /**
     * 更新操作
     *
     * @param map 实体参数
     */
    void update(Map<String, Object> map);


    /**
     * 添加或者更新
     *
     * @param entity 实体
     */
    void insertOrUpdate(E entity);

    /**
     * 插入或者更新一个实体，当此实体在数据库中存在时候更新，不存在时候插入，相当于SaveOrUpdate操作。
     *
     * @param entity 实体
     * @return 返回插入或更新后的实体
     */
    E merage(E entity);

    /**
     * 删除对应主键id的实体
     *
     * @param pk 主键id值
     */
    void delete(PK pk);

    /**
     * 查询对应主键id的实体
     *
     * @param pk 主键id值
     * @return 返回对应的实体
     */
    E load(PK pk);

    /**
     * 查询对应其他唯一键查询实体
     *
     * @return 返回对应的实体
     */
    E load(Map<String, Object> params);

    /**
     * 批量插入实体
     *
     * @param entitySet 实体集合
     * @return 成功插入的记录数
     */
    int batchInsert(Collection<E> entitySet);

    /**
     * 批量更新实体
     *
     * @param entitySet 实体集合
     * @return 成功更新的记录数
     */
    int batchUpdate(Collection<E> entitySet);

    /**
     * 批量更新实体,用于个性化字段的批量更新，比如大批量的数据需要更改“状态”字段，此方法可以大大提高执行效率。
     * 注意：开发需要自己保证map参数的正确性，否则可能抛出语句异常。
     * 如果批量太大，则发生异常情况下，不一定能回滚。
     *
     * @param stmtId  SQL的id
     * @param listMap map参数的集合
     */
    int batchUpdate(String stmtId, Collection<Map<String, Object>> listMap);

    /**
     * 批量删除实体
     *
     * @param keySet 主键id的集合
     * @return 成功删除的记录数
     */
    int batchDelete(Collection<PK> keySet);

    /**
     * 查询，根据Map类型的参数查询实体，返回实体结果集；如果需要分页，需要将分页参数“sortColumns”加入到params中。<p/>
     * 例如：params.put("sortColumns","username desc,id");
     *
     * @param params Map类型的参数
     * @return 实体结果集
     */
    List<E> query(Map<String, Object> params);

    /**
     * 统计查询记录数
     *
     * @param params
     * @return
     */
    int count(Map<String, Object> params);

    /**
     * 分页查询，根据Map类型的参数查询实体，返回实体结果集；如果需要分页，需要将分页参数“sortColumns”加入到params中。<p/>
     * 例如：params.put("sortColumns","username desc,id");
     *
     * @param params   Map类型的参数
     * @param startRow 起始行数（不含起始行的数据）
     * @param rowSize  要查询记录数
     * @return 实体结果集
     */
    List<E> query(Map<String, Object> params, int startRow, int rowSize);

    /**
     * 查询，根据SQL的id和参数集查询，没有实现物理分页，如果需要物理分页，需要调用带分页参数的同名方法，返回自定义的对象类型，通常为了满足个性化的查询而设计。<p/>
     * 如果需要排序，需要自己实现，建议还用sortColumns做为排序属性名称。
     *
     * @param stmtId SQL的id
     * @param params Map类型的参数，如果没有查询条件约束，则传递null也可以
     * @param <T>    自定义的对象类型
     * @return 对象结果集
     */
    <T> List<T> query(String stmtId, Map<String, Object> params);

    /**
     * 统计查询记录数，为定义查询设计
     *
     * @param stmtId SQL的id
     * @param params Map类型的参数，如果没有查询条件约束，则传递null也可以
     * @return 查询记录数
     */
    int count(String stmtId, Map<String, Object> params);

    /**
     * 查询，根据SQL的id和参数集查询，返回自定义的对象类型，通常为了满足个性化的查询而设计。<p/>
     * 如果需要排序，需要自己实现，建议还用sortColumns做为排序属性名称。
     *
     * @param stmtId SQL的id
     * @param params Map类型的参数，如果没有查询条件约束，则传递null也可以
     * @param <T>    自定义的对象类型
     * @return 对象结果集
     */
    <T> List<T> query(String stmtId, Map<String, Object> params, int startRow, int rowSize);

    /**
     * 判断是否唯一
     *
     * @param entity              实体
     * @param uniquePropertyNames 判断属性名称
     * @return boolean
     * @throws BaseException
     */
    boolean isUnique(E entity, String uniquePropertyNames);
}