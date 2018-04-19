package ${basePackage}.core.base;
import ${basePackage}.core.tools.StringTools;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 2016-3-24 10:07:18
 */
@Transactional
@Repository
public class BaseDaoImpl<T> extends HibernateDaoSupport implements BaseDao<T> {

    @Autowired
    public void setSessionFactoryOverride(SessionFactory sessionFactory) {
        super.setSessionFactory(sessionFactory);
    }

    @Override
    public void merge(T obj) {
        getHibernateTemplate().merge(obj);
    }

    @Override
    public void save(T obj) {
        getHibernateTemplate().save(obj);
    }

    @Override
    public void saveOrUpdate(T obj) {
        getHibernateTemplate().saveOrUpdate(obj);
    }

    @Override
    public void update(T obj) {
        getHibernateTemplate().update(obj);

    }

    @Override
    public void deleteById(Class<T> obj, Integer id) {
        getHibernateTemplate().delete(selectById(obj, id));

    }

    @Override
    public void delete(T obj) {
        getHibernateTemplate().delete(obj);

    }


    @Override
    public void deleteAll(List<T> list) {
        getHibernateTemplate().deleteAll(list);

    }

    @Override
    public T selectById(Class<T> clazz, Integer id) {
        T objs = (T) getHibernateTemplate().get(clazz, id);
        return objs;
    }


    @Override
    public T select(Class<T> clazz, Map<String, Object> paras) {
        List<T> list = this.selectAll(clazz, paras);
        if (list == null || list.size() == 0) return null;
        return list.get(0);
    }

    /**
     * 根据id返回对象
     * xx_like 模糊查询xx字段
     * xx_gt   大于查询xx字段
     * xx_lt   小于查询xx字段
     * xx_ge   大于等于查询xx字段
     * xx_le   小于等于查询xx字段
     * sql    拼接的原装sql
     * order 排序标识 如果多个只有一个有效
     * group 分组标识 如果多个只有一个有效
     * limit 条数标识 如果多个只有一个有效
     *
     * @param paras 实体类对象
     * @return
     */
    @Override
    public List<T> selectAll(Class<T> clazz, Map<String, Object> paras) {
        String hsql = this.packHsql(clazz, paras);
        Session session = getHibernateTemplate().getSessionFactory().openSession();
        Query query = session.createQuery(hsql);
        List<T> list = query.list();
        session.close();
        return list;
    }

    /**
     * 查询count对象
     * xx_like 模糊查询xx字段
     * xx_gt   大于查询xx字段
     * xx_lt   小于查询xx字段
     * order 排序标识
     * group 分组标识
     * limit 条数标识
     *
     * @param paras 实体类对象
     * @return
     */
    @Override
    public int count(Class<T> clazz, Map<String, Object> paras) {
        Session session = getHibernateTemplate().getSessionFactory().openSession();
        String queryCountHql = "select count(*) " + this.packHsql(clazz, paras);
        Query queryCount = session.createQuery(queryCountHql);
        List<?> countlist = queryCount.list();
        Integer totalCount = Integer.parseInt(countlist.get(0).toString());
        session.close();
        return totalCount;
    }

    /**
     * 查询sum数值
     * xx_like 模糊查询xx字段
     * xx_gt   大于查询xx字段
     * xx_lt   小于查询xx字段
     * order 排序标识
     * group 分组标识
     * limit 条数标识
     *
     * @param paras 实体类对象
     * @return
     */
    @Override
    public Long sum(Class<T> clazz, Map<String, Object> paras, String sumfield) {
        if (StringUtils.isBlank(sumfield)) return 0L;
        Session session = getHibernateTemplate().getSessionFactory().openSession();
        String queryCountHql = "select sum(" + sumfield + ") " + this.packHsql(clazz, paras);
        Query queryCount = session.createQuery(queryCountHql);
        List<?> countlist = queryCount.list();
        Long totalCount = 0L;
        if (countlist.get(0) != null) {
            totalCount = Long.parseLong(countlist.get(0).toString());
        }
        session.close();
        return totalCount;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map> selectByHql(String hql, Object[] objects) throws Exception {
        if (hql.toLowerCase().trim().startsWith("select")) {
            Session session = getHibernateTemplate().getSessionFactory().openSession();
            Query query = session.createQuery(hql);
            if (objects != null) {
                for (int i = 0; i < objects.length; i++) {
                    query.setParameter(i, objects[i]);
                }
            }
            List<Object[]> list = query.list();
            return convertToMap(hql, list);
        } else {
            throw new Exception("未检测到SELECT命令");
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public Page selectPage(Class<T> clazz, Page page) {
        if (page == null) {
            page = new Page();
        } else {
            //获取参数
            Map<String, Object> paras = page.getParams();
            if (paras == null || paras.isEmpty()) paras = new HashMap<String, Object>();
            //查询数据
            int totalCount = this.count(clazz, paras);
            // 返回分页对象
            Session session = getHibernateTemplate().getSessionFactory().openSession();
            String hsql = this.packHsql(clazz, paras);
            Query query = session.createQuery(hsql);
            page = new Page(page.getPageSize(), page.getCurPage(), totalCount);
            int startRow = (page.getCurPage() - 1) * page.getPageSize();
            //分页操作;
            query.setFirstResult(startRow);
            query.setMaxResults(page.getPageSize());
            page.setVoList(query.list());
            session.close();
        }
        return page;
    }


    @SuppressWarnings("unchecked")
    @Override
    public Page selectPageByHql(String hql, Integer pageNo, Integer pageSize, Object[] objects) {
        Session session = getHibernateTemplate().getSessionFactory().openSession();
        String queryCountHql = " select count(*) " + StringTools.removeSelectToPageQuery(StringTools.removeOrdersToPageQuery(hql));
        Query queryCount = session.createQuery(queryCountHql);
        if (objects != null) {
            for (int i = 0; i < objects.length; i++) {
                queryCount.setParameter(i, objects[i]);
            }
        }
        List<?> countlist = queryCount.list();
        Integer totalCount = Integer.parseInt(countlist.get(0).toString());
        // 返回分页对象
        if (totalCount < 1)
            return new Page();

        Query query = session.createQuery(hql);
        if (objects != null) {
            for (int i = 0; i < objects.length; i++) {
                query.setParameter(i, objects[i]);
            }
        }
        Page page = new Page(pageSize, pageNo, totalCount);
        int startRow = (page.getCurPage() - 1) * page.getPageSize();
        //分页操作;
        query.setFirstResult(startRow);
        query.setMaxResults(pageSize);
        page.setVoList(convertToMap(hql, query.list()));
        session.close();
        return page;
    }

    @Override
    public List selectBySql(String sql) {
        Session session = getHibernateTemplate().getSessionFactory().openSession();
        Query query = session.createSQLQuery(sql);
//        List<Object[]> list = query.list();
        List list = query.list();
        session.close();
//        return convertToMap(sql,list);
        return list;
    }

    @Override
    public Page selectPageBySql(String sql, Integer pageNo, Integer pageSize) {
        Session session = getHibernateTemplate().getSessionFactory().openSession();
        String queryCountHql = " select count(*) " + StringTools.removeSelectToPageQuery(StringTools.removeOrdersToPageQuery(sql));
        Query queryCount = session.createSQLQuery(queryCountHql);
        List<?> countlist = queryCount.list();
        Integer totalCount = Integer.parseInt(countlist.get(0).toString());
        // 返回分页对象
        if (totalCount < 1)
            return new Page();
        Query query = session.createSQLQuery(sql);
        Page page = new Page(pageSize, pageNo, totalCount);
        int startRow = (page.getCurPage() - 1) * page.getPageSize();
        //分页操作;
        query.setFirstResult(startRow);
        query.setMaxResults(pageSize);
//        page.setVoList(convertToMap(sql,query.list()));
        page.setVoList(query.list());
        session.close();
        return page;
    }

    private static List<Map> convertToMap(String shql, List<Object[]> list) {
        List<Map> resultList = new ArrayList<>();
        shql = shql.toLowerCase().trim();
        String key = shql.substring(shql.indexOf("select") + 6, shql.indexOf("from"));
        String[] keys = key.trim().split(",");
        Map<String, Object> map;
        for (Object[] objs : list) {
            map = new HashedMap();
            for (int i = 0; i < objs.length; i++) {
                map.put(keys[i].substring(keys[i].indexOf(".") + 1).trim(), objs[i]);
            }
            resultList.add(map);
        }
        return resultList;
    }

    @Override
    public boolean executeHql(String hql, Object[] objects) {
        Session session = getHibernateTemplate().getSessionFactory().openSession();
        Query query = session.createQuery(hql);
        if (objects != null) {
            for (int i = 0; i < objects.length; i++) {
                query.setParameter(i, objects[i]);
            }
        }
        int rows = query.executeUpdate();
        session.close();
        return rows > 0;
    }

    @Override
    public boolean executeSql(String sql) {
        Session session = getHibernateTemplate().getSessionFactory().openSession();
        Query query = session.createSQLQuery(sql);
        int rows = query.executeUpdate();
        session.close();
        return rows > 0;
    }

    /**
     * 根据id返回对象
     * xx_like 模糊查询xx字段
     * xx_in   in查询xx
     * xx_notin   notin查询xx
     * xx_neq   不等于查询xx
     * xx_gt   大于查询xx字段
     * xx_lt   小于查询xx字段
     * xx_ge   大于等于查询xx字段
     * xx_le   小于等于查询xx字段
     * sql    拼接的原装sql
     * order 排序标识 如果多个只有一个有效
     * group 分组标识 如果多个只有一个有效
     * limit 条数标识 如果多个只有一个有效
     *
     * @param paras 实体类对象
     * @return
     */
    private String packHsql(Class clazz, Map<String, Object> paras) {
        StringBuffer orderBys = new StringBuffer(" order by id desc ");
        StringBuffer groupBys = new StringBuffer();
        StringBuffer limits = new StringBuffer();
        StringBuffer hql = new StringBuffer("From ");
        hql.append(clazz.getName());
        if (!(paras == null || paras.isEmpty())) {
            hql.append(" where 1=1 ");
            Set<String> set = paras.keySet();
            for (String key : set) {
                if (key.contains("_like")) {
                    hql.append(" and ");
                    hql.append(key.replace("_like", ""));
                    hql.append(" like '");
                    hql.append(paras.get(key));
                    hql.append("' ");
                } else if (key.contains("_in")) {
                    hql.append(" and ");
                    hql.append(key.replace("_in", ""));
                    hql.append(" in (");
                    hql.append(paras.get(key));
                    hql.append(") ");
                } else if (key.contains("_notin")) {
                    hql.append(" and ");
                    hql.append(key.replace("_notin", ""));
                    hql.append(" not in (");
                    hql.append(paras.get(key));
                    hql.append(") ");
                } else if (key.contains("_neq")) {
                    hql.append(" and ");
                    hql.append(key.replace("_neq", ""));
                    hql.append(" != '");
                    hql.append(paras.get(key));
                    hql.append("' ");
                } else if (key.contains("_gt")) {
                    hql.append(" and ");
                    hql.append(key.replace("_gt", ""));
                    hql.append(" > ");
                    hql.append(paras.get(key));
                } else if (key.contains("_lt")) {
                    hql.append(" and ");
                    hql.append(key.replace("_lt", ""));
                    hql.append(" < ");
                    hql.append(paras.get(key));
                } else if (key.contains("_ge")) {
                    hql.append(" and ");
                    hql.append(key.replace("_ge", ""));
                    hql.append(" >= ");
                    hql.append(paras.get(key));
                } else if (key.contains("_le")) {
                    hql.append(" and ");
                    hql.append(key.replace("_le", ""));
                    hql.append(" <= ");
                    hql.append(paras.get(key));
                } else if (key.equals("sql")) {
                    hql.append(" and ");
                    hql.append(" (");
                    hql.append(paras.get(key));
                    hql.append(" ) ");
                } else if (key.equals("order")) {
                    orderBys = new StringBuffer(" order by ");
                    orderBys.append(paras.get(key));
                } else if (key.equals("group")) {
                    groupBys = new StringBuffer(" group by ");
                    groupBys.append(paras.get(key));
                } else {
                    hql.append(" and ");
                    hql.append(key);
                    hql.append(" = '");
                    hql.append(paras.get(key));
                    hql.append("' ");
                }
            }
            hql.append(orderBys);
            hql.append(groupBys);
            hql.append(limits);
        }
        return hql.toString();
    }
}
