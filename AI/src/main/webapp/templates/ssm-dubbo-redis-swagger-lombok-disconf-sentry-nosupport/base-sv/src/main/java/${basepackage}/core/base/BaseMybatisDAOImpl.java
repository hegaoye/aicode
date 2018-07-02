package ${basePackage}.core.base;


import ${basePackage}.core.exceptions.BaseException;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 通用DAO的iBatis实现，这个适用范围相对WebBaseIBatisDAO更为广泛，这个更适合用于纯Java的应用中.
 *
 * @author lixin 11-12-12 下午10:42
 */
public abstract class BaseMybatisDAOImpl<E, PK extends Serializable> extends SqlSessionDaoSupport implements BaseMybatisDAO<E, PK> {
    protected final static Logger logger = LoggerFactory.getLogger(BaseMybatisSVImpl.class);

    protected String sqlmapNamespace;
    protected Class<?> entityType;


    @Autowired
    @Override
    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        super.setSqlSessionTemplate(sqlSessionTemplate);
    }

    protected <S> S getMapper(Class<S> clazz) {
        return getSqlSession().getMapper(clazz);
    }

    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }


    /**
     * 通过反射获取传递给超类的泛型
     */
    public BaseMybatisDAOImpl() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.entityType = (Class) pt.getActualTypeArguments()[0];
        this.sqlmapNamespace = this.entityType.getSimpleName();

    }


    public String getMybatisMapperNamesapce() {
        throw new RuntimeException("not yet implement");
    }

    /**
     * 保存实体
     *
     * @param entity 实体
     * @return
     */
    @Override
    public E insert(E entity) {
        logger.info("====> 保存 [void save(E entity)]");
        logger.info("====> " + ToStringBuilder.reflectionToString(entity, ToStringStyle.MULTI_LINE_STYLE));

        getSqlSession().insert(this.sqlmapNamespace + "." + SQLID_INSERT, entity);
        return entity;
    }

    /**
     * 更新实体
     *
     * @param entity 实体
     * @return
     */
    @Override
    public E update(E entity) {
        logger.info("====> 更新 [E update(E entity)]");
        logger.info("====> " + ToStringBuilder.reflectionToString(entity, ToStringStyle.MULTI_LINE_STYLE));

        getSqlSession().update(this.sqlmapNamespace + "." + SQLID_UPDATE, entity);
        return entity;
    }

    /**
     * 更新操作
     *
     * @param map 实体参数
     */
    @Override
    public void update(Map<String, Object> map) {
        logger.info("====> 更新操作 [void update(Map<String, Object> map)]");
        logger.info("====> " + ToStringBuilder.reflectionToString(map, ToStringStyle.MULTI_LINE_STYLE));
        getSqlSession().update(this.sqlmapNamespace + "." + SQLID_UPDATE, map);
    }


    /**
     * 添加或者更新
     *
     * @param entity 实体
     */
    @Override
    public void insertOrUpdate(E entity) {
        logger.info("====> 更新或保存 [void saveOrUpdate(E entity)]");
        logger.info("====> " + ToStringBuilder.reflectionToString(entity, ToStringStyle.MULTI_LINE_STYLE));

        if (((BaseEntity) entity).getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
    }

    /**
     * 合并实体
     *
     * @param entity 实体
     * @return
     */
    @Override
    public E merage(E entity) {
        logger.info("====> 合并实体 [E merage(E entity)]");
        logger.info("====> " + ToStringBuilder.reflectionToString(entity, ToStringStyle.MULTI_LINE_STYLE));
        if (entity instanceof BaseEntity) {
            BaseEntity e = (BaseEntity) entity;
            if (e.getId() == null || e.getId().longValue() == 0L) {
                getSqlSession().insert(this.sqlmapNamespace + "." + SQLID_INSERT, entity);
                return entity;
            } else {
                this.update(entity);
                return entity;
            }
        } else {
            throw new RuntimeException("merage方法只适用于BaseEntity类型的实体！请检查你的参数是否符合条件！");
        }
    }

    /**
     * 根据主键删除一条实体
     *
     * @param pk 主键id值
     */
    @Override
    public void delete(PK pk) {
        logger.info("====> 删除数据 [void delete(PK pk)]");
        logger.info("====> " + pk);
        getSqlSession().delete(this.sqlmapNamespace + "." + SQLID_DELETE, pk);
    }

    /**
     * 批量插入
     *
     * @param entitySet 集合
     * @return
     */
    @Override
    public int batchInsert(Collection<E> entitySet) {
        logger.info("====> 批量插入 [int batchInsert(Collection<E> entitySet)]");
        logger.info("====> " + ToStringBuilder.reflectionToString(entitySet, ToStringStyle.MULTI_LINE_STYLE));

        Iterator var2 = entitySet.iterator();

        while (var2.hasNext()) {
            Object entity = var2.next();
            getSqlSession().insert(this.sqlmapNamespace + "." + SQLID_INSERT, entity);
        }

        return entitySet.size();
    }


    /**
     * 批量更新
     *
     * @param entitySet
     * @return
     */
    @Override
    public int batchUpdate(Collection<E> entitySet) {
        int x = 0;

        Object entity;
        for (Iterator var3 = entitySet.iterator(); var3.hasNext(); x += getSqlSession().insert(this.sqlmapNamespace + "." + SQLID_UPDATE, entity)) {
            entity = var3.next();
        }

        return entitySet.size();
    }

    /**
     * 批量更新
     *
     * @param stmtId  SQL的id
     * @param listMap map参数的集合
     * @return
     */
    @Override
    public int batchUpdate(String stmtId, Collection<Map<String, Object>> listMap) {
        int x = 0;

        Map map;
        for (Iterator var4 = listMap.iterator(); var4.hasNext(); x += getSqlSession().update(this.sqlmapNamespace + "." + stmtId, map)) {
            map = (Map) var4.next();
        }

        return x;
    }

    /**
     * 批量删除
     *
     * @param keySet
     * @return
     */
    @Override
    public int batchDelete(Collection<PK> keySet) {
        logger.info("====> 批量删除 [int batchDelete(Collection<PK> keySet)");
        logger.info("====> " + ToStringBuilder.reflectionToString(keySet, ToStringStyle.MULTI_LINE_STYLE));

        int x = 0;
        Serializable pk;
        for (Iterator var3 = keySet.iterator(); var3.hasNext(); x += getSqlSession().update(this.sqlmapNamespace + "." + SQLID_DELETE, pk)) {
            pk = (Serializable) var3.next();
        }

        return x;
    }


    /**
     * 根据 map参数查询一个列表数据
     *
     * @param params Map类型的参数
     * @return
     */
    @Override
    public List<E> query(Map<String, Object> params) {
        logger.info("====> 查询所有数据 [List<E> query(Map<String, Object> params)]");
        logger.info("====> " + ToStringBuilder.reflectionToString(params, ToStringStyle.MULTI_LINE_STYLE));

        List<E> list = getSqlSession().selectList(this.sqlmapNamespace + "." + SQLID_QUERY, params);

        logger.info("<==== " + ToStringBuilder.reflectionToString(list, ToStringStyle.MULTI_LINE_STYLE));
        return list;
    }

    /**
     * 根据主键查询一条实体
     *
     * @param pk 主键id值
     * @return
     */
    @Override
    public E load(PK pk) {
        logger.info("====> 根据id查询 [E load(PK pk)]");
        logger.info("====> " + pk);

        E e = getSqlSession().selectOne(this.sqlmapNamespace + "." + SQLID_LOAD, pk);

        logger.info("<==== 根据id查询 [E load(PK id)]");
        logger.info("<==== " + ToStringBuilder.reflectionToString(e, ToStringStyle.MULTI_LINE_STYLE));
        return e;
    }


    /**
     * 根据map参数获得一条实体
     *
     * @param params map参数
     * @return
     */
    @Override
    public E load(Map<String, Object> params) {
        logger.info("====> 条件查询实体 [E load(Map<String, Object> params)]");
        logger.info("====> " + ToStringBuilder.reflectionToString(params, ToStringStyle.MULTI_LINE_STYLE));

        List<E> list = this.query(params, 0, 1);
        if (list.size() == 0) {
            return null;
        }

        logger.info("<==== 条件查询实体 [E load(Map<String, Object> params)]");
        logger.info("<==== " + ToStringBuilder.reflectionToString(list, ToStringStyle.MULTI_LINE_STYLE));
        return list.get(0);
    }

    /**
     * 根据map参数统计记录条数
     *
     * @param params map 参数
     * @return
     */
    @Override
    public int count(Map<String, Object> params) {
        return Integer.parseInt(getSqlSession().selectOne(this.sqlmapNamespace + "." + SQLID_COUNT, params) + "");
    }


    /**
     * 分页查询集合
     *
     * @param params   Map类型的参数
     * @param startRow 起始行数（不含起始行的数据）
     * @param rowSize  要查询记录数
     * @return
     */
    @Override
    public List<E> query(Map<String, Object> params, int startRow, int rowSize) {
        logger.info("====> 分页查询 [Page<E> getList(Page<E> page)]");
        logger.info("====> " + ToStringBuilder.reflectionToString(params, ToStringStyle.MULTI_LINE_STYLE));

        List<E> list = getSqlSession().selectList(this.sqlmapNamespace + "." + SQLID_QUERY, params, new RowBounds(startRow, rowSize));

        logger.info("<==== 分页查询 [Page<E> getList(Page<E> page)]");
        logger.info("<==== " + ToStringBuilder.reflectionToString(list, ToStringStyle.MULTI_LINE_STYLE));
        return list;
    }

    /**
     * 根据 stmtid 查询一个集合
     *
     * @param stmtId SQL的id
     * @param params Map类型的参数，如果没有查询条件约束，则传递null也可以
     * @param <T>
     * @return
     */
    @Override
    public <T> List<T> query(String stmtId, Map<String, Object> params) {
        logger.info("====> 根据 stmtid 查询一个集合 [<T> List<T> query(String stmtId, Map<String, Object> params)]");
        logger.info("====> " + stmtId);
        logger.info("====> " + ToStringBuilder.reflectionToString(params, ToStringStyle.MULTI_LINE_STYLE));
        List<T> tList = getSqlSession().selectList(this.sqlmapNamespace + "." + stmtId, params);
        logger.info("<==== " + ToStringBuilder.reflectionToString(tList, ToStringStyle.MULTI_LINE_STYLE));
        return tList;
    }

    /**
     * 根据stmtid 进行统计条数
     *
     * @param stmtId SQL的id
     * @param params Map类型的参数，如果没有查询条件约束，则传递null也可以
     * @return
     */
    @Override
    public int count(String stmtId, Map<String, Object> params) {
        return Integer.parseInt(getSqlSession().selectOne(this.sqlmapNamespace + "." + stmtId, params) + "");
    }

    /**
     * 根据stmtid 进行分页查询
     *
     * @param stmtId   SQL的id
     * @param params   Map类型的参数，如果没有查询条件约束，则传递null也可以
     * @param startRow 开始条数
     * @param rowSize  向后查几条
     * @param <T>
     * @return
     */
    @Override
    public <T> List<T> query(String stmtId, Map<String, Object> params, int startRow, int rowSize) {
        logger.info("====> 根据stmtid 进行分页查询 [<T> List<T> query(String stmtId, Map<String, Object> params, int startRow, int rowSize)]");
        logger.info("====> " + stmtId);
        logger.info("====> " + startRow);
        logger.info("====> " + rowSize);
        logger.info("====> " + ToStringBuilder.reflectionToString(params, ToStringStyle.MULTI_LINE_STYLE));

        List<T> tList = getSqlSession().selectList(this.sqlmapNamespace + "." + stmtId, params, new RowBounds(startRow, rowSize));

        logger.info("<==== " + ToStringBuilder.reflectionToString(tList, ToStringStyle.MULTI_LINE_STYLE));
        return tList;
    }

    /**
     * 判断是否唯一
     *
     * @param entity              实体
     * @param uniquePropertyNames 判断属性名称
     * @return boolean
     * @throws BaseException
     */
    @Override
    public boolean isUnique(E entity, String uniquePropertyNames) {
        logger.info("====> 判断是否唯一 [boolean isUnique(E entity, String uniquePropertyNames)]");
        logger.info("====> " + ToStringBuilder.reflectionToString(entity, ToStringStyle.MULTI_LINE_STYLE));
        logger.info("====> " + ToStringBuilder.reflectionToString(uniquePropertyNames, ToStringStyle.MULTI_LINE_STYLE));
        boolean flag = getSqlSession().selectOne(this.sqlmapNamespace + "." + SQLID_ISUNIQUE, entity);
        logger.info("====> 判断是否唯一 [boolean isUnique(E entity, String uniquePropertyNames)]");
        logger.info("====> " + flag);
        return flag;

    }


}
