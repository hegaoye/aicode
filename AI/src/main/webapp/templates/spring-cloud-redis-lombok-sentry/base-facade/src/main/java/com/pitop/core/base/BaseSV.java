package ${basePackage}.core.base;

import ${basePackage}.core.exceptions.BaseException;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by lixin on 2017/6/22 0022. hegaoye@qq.com
 * 基础接口声明
 */
public interface BaseSV<E, PK extends Serializable> {

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
    void modify(E entity) throws BaseException;


    /**
     * 根据id查询实体
     *
     * @param param 实体的属性
     * @return E 实体
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    E load(Map<String, Object> param) throws BaseException;

    /**
     * 查询所有数据
     *
     * @return List<E>
     * @throws BaseException
     */
    List<E> listAll() throws BaseException;


    /**
     * 根据条件查询 集合
     *
     * @param map 对象类型的参数
     * @return List<E>
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    List<E> list(Map<String, Object> map) throws BaseException;


    /**
     * 根据条件查询进行分页
     *
     * @param map    对象类型的参数
     * @param offset 开始行
     * @param limit  步长
     * @return List<E>
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    List<E> list(Map<String, Object> map, int offset, int limit) throws BaseException;

    int count(Map<String, Object> map);


    /**
     * 保存或者更新实体
     *
     * @param entity 实体
     * @throws BaseException
     */
    @Transactional(readOnly = true)
    void saveOrUpdate(E entity) throws BaseException;


}
