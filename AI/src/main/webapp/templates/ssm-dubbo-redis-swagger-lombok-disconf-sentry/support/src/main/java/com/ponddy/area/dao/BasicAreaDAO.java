/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *       郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *       代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *       本代码仅用于龐帝業務系统.目.
 */



package ${basePackage}.area.dao;

import ${basePackage}.area.entity.BasicArea;
import ${basePackage}.core.base.BaseMybatisDAOImpl;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public class BasicAreaDAO extends BaseMybatisDAOImpl<BasicArea, String> {

    //根据areaCode获得区域对象
    public List<BasicArea> queryAll() {
        return getSqlSession().selectList(sqlmapNamespace + ".query");
    }

    //根据areaCode获得区域对象
    public BasicArea loadAreaCode(String areaCode) {
        return getSqlSession().selectOne(sqlmapNamespace + ".loadAreaCode", areaCode);
    }

    //省
    public BasicArea loadProvince(String province) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("province", province);
        map.put("level", 1);
        return getSqlSession().selectOne(sqlmapNamespace + ".loadAreaCodeByLevel", map);
    }

    //市
    public List<BasicArea> loadCity(String province) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("province", province);
        map.put("level", 2);
        return getSqlSession().selectList(sqlmapNamespace + ".loadAreaCodeByLevel", map);
    }

    //区
    public List<BasicArea> loadArea(String province) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("province", province);
        map.put("level", 3);
        return getSqlSession().selectList(sqlmapNamespace + ".loadAreaCodeByLevel", map);
    }

    public List<BasicArea> loadBasicAreaAndChildren(String province, int level) {
        return null;
    }

    //获取所有省份
    public List<BasicArea> listProvoice() {
        return getSqlSession().selectList(sqlmapNamespace + ".listProvoice");
    }
}
