/*
 * Copyright (c) 2016. 郑州仁中和科技有限公司.保留所有权利.
 *                       http://www.rzhkj.com/
 *      郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *      代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *      本代码仅用于河南中裕广恒科技股份有限公司的智慧粮仓项目.
 */
package com.zygh.zhlk.service.base.impl;

import com.zygh.zhlk.dao.BaseDao;
import com.zygh.zhlk.dao.LogDao;
import com.zygh.zhlk.dao.StorageDao;
import com.zygh.zhlk.entity.base.Page;
import com.zygh.zhlk.entity.gain.LaStorageEntity;
import com.zygh.zhlk.entity.sys.BssLogMdlEntity;
import com.zygh.zhlk.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @version 1.0.0
 *          2016年3月17日15:20:08
 */
public class BaseServiceImpl<T> implements BaseService<T> {
    @Autowired
    public BaseDao<T> baseDao;

    @Override
    public void merge(T obj) {
        baseDao.merge(obj);
    }

    @Override
    public void save(T obj) {
        baseDao.save(obj);
    }

    @Override
    public void saveOrUpdate(T obj) {
        baseDao.saveOrUpdate(obj);
    }

    @Override
    public void update(T obj) {
        baseDao.update(obj);
    }

    @Override
    public void deleteById(Class<T> clazz, Integer id) {
        baseDao.deleteById(clazz, id);
    }

    @Override
    public void delete(T obj) {
        baseDao.delete(obj);
    }
//
//    @Override
//    public void deleteAll(List<T> list) {
//        baseDao.deleteAll(list);
//    }

    @Override
    public T queryById(Class<T> clazz, Integer id) {
        return baseDao.selectById(clazz, id);
    }


    @Override
    public T query(Class<T> clazz, T t) {
        Map paras= JSON.parseObject(JSON.toJSONString(t));
        return (T) query(clazz,paras);
    }

    @Override
    public List<T> queryList(Class<T> clazz, T t) {
        Map paras= JSON.parseObject(JSON.toJSONString(t));
        return queryList(clazz,paras);
    }

    @Override
    public int count(Class<T> clazz, T t){
        Map paras= JSON.parseObject(JSON.toJSONString(t));
        return baseDao.count(clazz,paras);
    }

    @Override
    public T query(Class<T> clazz,Map<String,Object> paras) {
        return baseDao.select(clazz,paras);
    }

    @Override
    public List<T> queryList(Class<T> clazz,Map<String,Object> paras) {
        return baseDao.selectAll(clazz,paras);
    }

    @Override
    public int count(Class<T> clazz,Map<String,Object> paras){
        return baseDao.count(clazz,paras);
    }
    @Override
    public Long sum(Class<T> clazz,Map<String,Object> paras,String sumfield){
        return baseDao.sum(clazz,paras,sumfield);
    }
    @Override
    public List<Map> queryByHql(String hql, Object[] objects) throws Exception {
            return baseDao.selectByHql(hql, objects);
    }

    @Override
    public Page queryPage(Class<T> clazz, Page page) {
        return baseDao.selectPage(clazz,page);
    }

    @Override
    public Page queryPageByHql(String hql, Integer pageNo, Integer pageSize,
                                Object[] objects) {
        return baseDao.selectPageByHql(hql, pageNo, pageSize, objects);
    }

    @Override
    public List queryBySql(String sql) {
        return baseDao.selectBySql(sql);
    }

    @Override
    public Page queryPageBySql(String sql, Integer pageNo, Integer pageSize) {
        return baseDao.selectPageBySql(sql, pageNo, pageSize);
    }

    @Override
    public boolean executeHql(String hql, Object[] objects) {
        return baseDao.executeHql(hql, objects);
    }

    @Override
    public boolean executeSql(String sql) {
        return baseDao.executeSql(sql);
    }

}
