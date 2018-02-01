package ${basePackage}.core.base;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.fastjson.JSON;
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
