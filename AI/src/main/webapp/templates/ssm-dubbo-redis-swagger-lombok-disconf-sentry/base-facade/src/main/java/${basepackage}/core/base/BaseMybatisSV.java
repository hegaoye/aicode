package ${basePackage}.core.base;

import ${basePackage}.core.entity.Page;
import ${basePackage}.core.exceptions.BaseException;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by lixin on 2017/6/22 0022. hegaoye@qq.com
 * 基础接口声明
 */
public interface BaseMybatisSV<E, PK extends Serializable> {

    /**
     * 数据保存
     *
     * @param entity 实体
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    void save(E entity) throws BaseException;


    /**
     * 数据更新
     *
     * @param entity 实体
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    void update(E entity) throws BaseException;


    /**
     * 根据id查询实体
     *
     * @param id
     * @return
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    E load(PK id) throws BaseException;


    /**
     * 根据id查询实体
     *
     * @param param
     * @return
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    E load(Map<String, Object> param) throws BaseException;

    /**
     * 查询所有数据
     *
     * @return
     * @throws BaseException
     */
    List<E> getAll() throws BaseException;

    /**
     * 分页查询
     *
     * @param page 分页对象
     * @return List<E>
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    Page<E> getList(Page<E> page) throws BaseException;


    /**
     * @author 立坤 更新于2017.07.21
     * @remark 根据条件查询
     *
     * @param map
     * @return
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    List<E> queryList(Map<String, Object> map) throws BaseException;


    /**
     * 根据id判断是插入还是更新
     *
     * @param entity 实体
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    void saveOrUpdate(E entity) throws BaseException;



    /**
     * 根据id进行删除数据
     *
     * @param id 对象id
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    void deleteById(PK id) throws BaseException;

    /**
     * @author 立坤 更新于2017.07.21
     * @remark 批量删除信息
     *
     * @param ids
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    void deleteByIds(List<Long> ids) throws BaseException;

    /**
     * 判断是否唯一
     *
     * @param entity              实体
     * @param uniquePropertyNames 判断属性名称
     * @return boolean
     * @throws BaseException
     */

    @Transactional(readOnly = true)
    boolean isUnique(E entity, String uniquePropertyNames) throws BaseException;
}
