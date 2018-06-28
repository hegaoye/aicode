package ${basePackage}.core.base;

import ${basePackage}.core.exceptions.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 基础接口实现
 *
 * @author lixin hegaoye@qq.com
 */
@Transactional
@Slf4j
public abstract class BaseSVImpl<E, PK extends Serializable> implements BaseSV<E, PK> {

    /**
     * 获得dao对象
     *
     * @return BaseDAO
     */
    protected abstract BaseDAO getBaseDAO();//获得当前dao对象


    /**
     * 根据id查询实体
     *
     * @param param 实体的属性
     * @return E 实体
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    @Override
    public E load(Map<String, Object> param) throws BaseException {
        E e = (E) getBaseDAO().load(param);
        return e;
    }

    /**
     * 查询所有数据
     *
     * @return List<E>
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    @Override
    public List<E> listAll() throws BaseException {
        return getBaseDAO().list(new HashMap());
    }


    /**
     * 根据条件查询 集合
     *
     * @param map 对象类型的参数
     * @return List<E>
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    @Override
    public List<E> list(Map<String, Object> map) throws BaseException {
        List<E> list = getBaseDAO().list(map);
        return list;
    }

    /**
     * 根据条件查询进行分页
     *
     * @param map    对象类型的参数
     * @param offset 开始行
     * @param limit  步长
     * @return List<E>
     * @throws BaseException
     */
    @Override
    public List<E> list(Map<String, Object> map, int offset, int limit) throws BaseException {
        return getBaseDAO().list(map, new RowBounds(offset, limit));
    }

    @Override
    public int count(Map<String, Object> map) {
        return getBaseDAO().count(map);
    }

    /**
     * 保存或者更新实体
     *
     * @param entity 实体
     * @throws BaseException
     */
    @Override
    public void saveOrUpdate(E entity) throws BaseException {
        if (entity == null) {
            throw new BaseException("对象为空异常！");
        }

        if (((BaseEntity) entity).getId() == null) {
            getBaseDAO().insert(entity);
        } else {
            getBaseDAO().update(entity);
        }
    }


    /**
     * 数据保存
     *
     * @param entity 实体
     * @throws BaseException
     */
    @Override
    public void save(E entity) throws BaseException {
        getBaseDAO().insert(entity);
    }


    /**
     * 数据更新
     *
     * @param entity 实体
     * @throws BaseException
     */
    @Override
    public void modify(E entity) throws BaseException {
        getBaseDAO().update(entity);
    }


}
