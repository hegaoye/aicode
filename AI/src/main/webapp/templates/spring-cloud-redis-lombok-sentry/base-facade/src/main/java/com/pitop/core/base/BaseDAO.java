package ${basePackage}.core.base;

import org.apache.ibatis.session.RowBounds;
import org.springframework.data.repository.NoRepositoryBean;

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
@NoRepositoryBean
public interface BaseDAO<E, PK extends Serializable> {

    /**
     * 插入操作，将一个实体保存到数据库中，当这个实体的主键是自增或序列类型时候，会自动回填主键ID，并返回。<p/>
     * 反之，只保存实体，并返回该实体。
     *
     * @param entity 实体
     */
    void insert(E entity);


    /**
     * 批量插入实体
     *
     * @param entitySet 实体集合
     * @return 成功插入的记录数
     */
    int batchInsert(Collection<E> entitySet);

    /**
     * 更新操作，将一个实体更新到数据库对应ID的记录上，此更新字段与SQLMap配置的字段有关。
     *
     * @param entity 实体
     * @return 返回更细的实体
     */
    void update(E entity);

    /**
     * 更新操作
     *
     * @param map 实体参数
     */
    void update(Map<String, Object> map);


    /**
     * 查询对应其他唯一键查询实体
     *
     * @return 返回对应的实体
     */
    E load(Map<String, Object> params);


    /**
     * 查询，根据Map类型的参数查询实体，返回实体结果集；如果需要分页，需要将分页参数“sortColumns”加入到params中。<p/>
     * 例如：params.put("sortColumns","username desc,id");
     *
     * @param params Map类型的参数
     * @return 实体结果集
     */
    List<E> list(Map<String, Object> params);


    /**
     * 分页查询，根据Map类型的参数查询实体，返回实体结果集；如果需要分页，需要将分页参数“sortColumns”加入到params中。<p/>
     * 例如：params.put("sortColumns","username desc,id");
     *
     * @param params   设置实体的属性
     * @param rowBounds   分页对象，需要传递 startRow 起始行数（不含起始行的数据） rowSize  要查询记录数
     * @return 实体结果集
     */
    List<E> list(Map<String, Object> params, RowBounds rowBounds);

    /**
     * 统计查询记录数
     *
     * @param params 设置实体的属性
     * @return
     */
    int count(Map<String, Object> params);

}
