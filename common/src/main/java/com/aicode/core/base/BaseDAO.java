package com.aicode.core.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 通用DAO接口.
 *
 * @author hegaoye 11-12-12 下午10:37
 */
@Slf4j
public abstract class BaseDAO<M extends BaseMapper<E>, E> implements BaseMapper<E>  {

    @Autowired
    protected M mapper;

    /**
     * 分页查询
     *
     * @param queryWrapper 分页查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<E>
     */
    public List<E> list(QueryWrapper<E> queryWrapper, int offset, int limit) {
        return mapper.selectList(queryWrapper.lambda().last("limit " + offset + "," + limit));
    }

    @Override
    public int insert(E entity) {
        return mapper.insert(entity);
    }

    @Override
    public int deleteById(Serializable id) {
        return mapper.deleteById(id);
    }

    @Override
    public int deleteByMap(Map<String, Object> columnMap) {
        return mapper.deleteByMap(columnMap);
    }

    @Override
    public int delete(Wrapper<E> wrapper) {
        return mapper.delete(wrapper);
    }

    @Override
    public int deleteBatchIds(Collection<? extends Serializable> idList) {
        return mapper.deleteBatchIds(idList);
    }

    @Override
    public int updateById(E entity) {
        return mapper.updateById(entity);
    }

    @Override
    public int update(E entity, Wrapper<E> updateWrapper) {
        return mapper.update(entity, updateWrapper);
    }

    @Override
    public E selectById(Serializable id) {
        return mapper.selectById(id);
    }

    @Override
    public List<E> selectBatchIds(Collection<? extends Serializable> idList) {
        return mapper.selectBatchIds(idList);
    }

    @Override
    public List<E> selectByMap(Map<String, Object> columnMap) {
        return mapper.selectByMap(columnMap);
    }

    @Override
    public E selectOne(Wrapper<E> queryWrapper) {
        return mapper.selectOne(queryWrapper);
    }

    @Override
    public Integer selectCount(Wrapper<E> queryWrapper) {
        return mapper.selectCount(queryWrapper);
    }

    @Override
    public List<E> selectList(Wrapper<E> queryWrapper) {
        return mapper.selectList(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> selectMaps(Wrapper<E> queryWrapper) {
        return mapper.selectMaps(queryWrapper);
    }

    @Override
    public List<Object> selectObjs(Wrapper<E> queryWrapper) {
        return mapper.selectObjs(queryWrapper);
    }

    @Override
    public IPage<E> selectPage(IPage<E> page, Wrapper<E> queryWrapper) {
        return mapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<Map<String, Object>> selectMapsPage(IPage<E> page, Wrapper<E> queryWrapper) {
        return mapper.selectMapsPage(page, queryWrapper);
    }

}
