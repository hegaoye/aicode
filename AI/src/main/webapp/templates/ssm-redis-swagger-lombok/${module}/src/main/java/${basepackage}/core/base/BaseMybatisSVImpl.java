package ${basePackage}.core.base;

import ${basePackage}.core.tools.redis.RedisUtils;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author lixin hegaoye@qq.com
 *         基础接口实现
 */
@Transactional
public abstract class BaseMybatisSVImpl<E, PK extends Serializable> implements BaseMybatisSV<E, PK> {
    protected final static Logger logger = LoggerFactory.getLogger(BaseMybatisSVImpl.class);

    @Resource
    protected RedisTemplate<String, Object> redisTemplate;
    @Resource
    protected RedisUtils redisUtils;

    /**
     * 获得dao对象
     *
     * @return BaseMybatisDAO
     */
    protected abstract BaseMybatisDAO getBaseMybatisDAO();//获得当前dao对象

    /**
     * 根据id查询实体
     *
     * @param id
     * @return
     * @
     */
    @Transactional(readOnly = true)
    @Override
    public E load(PK id) {
        if (id == null) {
            return null;
        }
        E e = (E) getBaseMybatisDAO().load(id);
        return e;
    }


    /**
     * 条件查询实体
     *
     * @param param
     * @return
     * @
     */
    @Transactional(readOnly = true)
    @Override
    public E load(Map<String, Object> param) {
        Assert.notEmpty(param, "查询参数为空！");
        E e = (E) getBaseMybatisDAO().load(param);
        return e;
    }

    /**
     * 查询所有数据
     *
     * @return
     * @
     */
    @Transactional(readOnly = true)
    @Override
    public List<E> getAll() {
        return getBaseMybatisDAO().query(new HashedMap());
    }

    /**
     * 分页查询
     *
     * @param page 分页对象
     * @return List<E>
     * @
     */

    @Override
    @Transactional(readOnly = true)
    public Page<E> getList(Page<E> page) {
        int i = getBaseMybatisDAO().count(page.getParams());
        page.setTotalRow(i);
        List<E> list = getBaseMybatisDAO().query(page.getParams(), page.genRowStart(), page.getPageSize());
        page.setVoList(list);
        return page;
    }


    /**
     * 根据条件查询
     *
     * @param map
     * @return
     * @
     * @remark 根据条件查询
     */
    @Transactional(readOnly = true)
    @Override
    public List<E> queryList(Map<String, Object> map) {
        List<E> list = getBaseMybatisDAO().query(map);
        return list;
    }

    /**
     * 根据id判断是插入还是更新
     * 更新或保存
     *
     * @param entity 实体
     * @
     */
    @Override
    public void saveOrUpdate(E entity) {
        getBaseMybatisDAO().insertOrUpdate(entity);
    }


    /**
     * 保存
     *
     * @param entity 实体
     * @
     */
    @Override
    public void save(E entity) {
        getBaseMybatisDAO().insert(entity);
    }


    /**
     * 更新
     *
     * @param entity 实体
     * @
     */
    @Override
    public void update(E entity) {
        getBaseMybatisDAO().update(entity);
    }

    /**
     * 根据id进行删除数据
     *
     * @param id 对象id
     * @
     */
    @Override
    public void deleteById(PK id) {
        getBaseMybatisDAO().delete(id);
    }

    /**
     * 批量删除信息
     *
     * @param ids ids 集合
     * @
     * @remark 批量删除信息
     */
    @Override
    public void deleteByIds(List<Long> ids) {
        getBaseMybatisDAO().batchDelete(ids);
    }

    /**
     * 判断是否唯一
     *
     * @param entity              实体
     * @param uniquePropertyNames 判断属性名称
     * @return boolean
     * @
     */
    @Transactional(readOnly = true)
    @Override
    public boolean isUnique(E entity, String uniquePropertyNames) {
        return getBaseMybatisDAO().isUnique(entity, uniquePropertyNames);
    }




    /**
     * 检查字段是否存在
     *
     * @param property    例如 phone 这项要和实体类对应，否则不起效
     * @param propertyVal 例如 13174108520
     * @return 存在返回true ;不存在返回false
     */
    @Override
    public boolean isExist(String property, String propertyVal) throws BaseException {
        if (StringUtils.isBlank(property)) {
            throw new BaseException("字段" + property + BaseException.ExceptionMessage.ParamIsEmpty);
        }
        if (StringUtils.isBlank(propertyVal)) {
            throw new BaseException("字段值" + BaseException.ExceptionMessage.ParamIsEmpty);
        }
        Map<String, Object> param = new HashedMap();
        param.put(property, propertyVal);
        int result = getBaseMybatisDAO().count(param);
        return result > 0;
    }

    /**
     * 根据条件集合 检查字段是否存在
     *
     * @param property      例如 phone 这项要和实体类对应，否则不起效
     * @param propertyVal   例如 13174108520
     * @param conditionsMap 条件集合
     * @return 存在返回true ;不存在返回false
     */
    @Override
    public boolean isExistByConditions(String property, String propertyVal, Map conditionsMap) throws BaseException {
        if (StringUtils.isBlank(property)) {
            throw new BaseException("字段" + property + BaseException.ExceptionMessage.ParamIsEmpty);
        }
        if (StringUtils.isBlank(propertyVal)) {
            throw new BaseException("字段值" + BaseException.ExceptionMessage.ParamIsEmpty);
        }
        Map<String, Object> param = new HashedMap();
        param.putAll(conditionsMap);
        param.put(property, propertyVal);
        int result = getBaseMybatisDAO().count(param);
        return result > 0;
    }

}
