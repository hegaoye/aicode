package ${basePackage}.core.base;

import ${basePackage}.core.entity.Page;
import ${basePackage}.core.exceptions.BaseException;
import ${basePackage}.core.tools.redis.RedisUtils;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
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
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    @Override
    public E load(PK id) throws BaseException {
        if (id == null) {
            throw new BaseException("PK 为0 或着为null异常！");
        }
        E e = (E) getBaseMybatisDAO().load(id);
        return e;
    }


    /**
     * 条件查询实体
     *
     * @param param
     * @return
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    @Override
    public E load(Map<String, Object> param) throws BaseException {
        Assert.notEmpty(param, "查询参数为空！");
        E e = (E) getBaseMybatisDAO().load(param);
        return e;
    }

    /**
     * 查询所有数据
     *
     * @return
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    @Override
    public List<E> getAll() throws BaseException {
        return getBaseMybatisDAO().query(new HashedMap());
    }

    /**
     * 分页查询
     *
     * @param page 分页对象
     * @return List<E>
     * @throws BaseException
     */

    @Transactional(readOnly = true)
    @Override
    public Page<E> getList(Page<E> page) throws BaseException {
        if (page == null) {
            throw new BaseException("page 为空对象！查询失败！");
        }
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
     * @throws BaseException
     * @remark 根据条件查询
     */
    @Transactional(readOnly = true)
    @Override
    public List<E> queryList(Map<String, Object> map) throws BaseException {
        List<E> list = getBaseMybatisDAO().query(map);
        return list;
    }

    /**
     * 根据id判断是插入还是更新
     * 更新或保存
     *
     * @param entity 实体
     * @throws BaseException
     */
    @Override
    public void saveOrUpdate(E entity) throws BaseException {
        if (entity == null) {
            throw new BaseException("对象为空异常！");
        }
        getBaseMybatisDAO().insertOrUpdate(entity);
    }


    /**
     * 保存
     *
     * @param entity 实体
     * @throws BaseException
     */
    @Override
    public void save(E entity) throws BaseException {
        getBaseMybatisDAO().insert(entity);
    }


    /**
     * 更新
     *
     * @param entity 实体
     * @throws BaseException
     */
    @Override
    public void update(E entity) throws BaseException {
        getBaseMybatisDAO().update(entity);
    }

    /**
     * 根据id进行删除数据
     *
     * @param id 对象id
     * @throws BaseException
     */
    @Override
    public void deleteById(PK id) throws BaseException {
        getBaseMybatisDAO().delete(id);
    }

    /**
     * 批量删除信息
     *
     * @param ids ids 集合
     * @throws BaseException
     * @remark 批量删除信息
     */
    @Override
    public void deleteByIds(List<Long> ids) throws BaseException {
        getBaseMybatisDAO().batchDelete(ids);
    }

    /**
     * 判断是否唯一
     *
     * @param entity              实体
     * @param uniquePropertyNames 判断属性名称
     * @return boolean
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    @Override
    public boolean isUnique(E entity, String uniquePropertyNames) throws BaseException {
        return getBaseMybatisDAO().isUnique(entity, uniquePropertyNames);
    }

}
