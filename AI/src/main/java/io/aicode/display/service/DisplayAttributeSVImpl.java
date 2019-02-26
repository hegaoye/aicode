/*
 * http://www.aicode.io
本代码仅用于AI-Code.
 */
package io.aicode.display.service;

import io.aicode.core.base.BaseMybatisDAO;
import io.aicode.core.base.BaseMybatisSVImpl;
import io.aicode.display.dao.DisplayAttributeDAO;
import io.aicode.display.entity.DisplayAttribute;
import io.aicode.display.facade.DisplayAttributeSV;
import io.aicode.project.dao.MapFieldColumnDAO;
import io.aicode.project.entity.MapFieldColumn;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * 显示属性
 *
 * @author lixin
 */
@Component
@Service
public class DisplayAttributeSVImpl extends BaseMybatisSVImpl<DisplayAttribute, Long> implements DisplayAttributeSV {


    @Resource
    private DisplayAttributeDAO displayAttributeDAO;

    @Resource
    private MapFieldColumnDAO mapFieldColumnDAO;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return displayAttributeDAO;
    }


    /**
     * 加载对象显示属性 通过mapFieldColumnCode
     *
     * @param mapFieldColumnCode 字段编码
     * @return DisplayAttribute
     */
    @Override
    public DisplayAttribute loadByMapFieldColumnCode(String mapFieldColumnCode) {
        return displayAttributeDAO.loadByMapFieldColumnCode(mapFieldColumnCode);
    }

    /**
     * 删除对象显示属性
     *
     * @param id * @param mapFieldColumnCode 字段编码
     */
    @Override
    public void delete(Long id, String mapFieldColumnCode) {
        displayAttributeDAO.delete(id, mapFieldColumnCode);
    }

    /**
     * 添加或修改
     * @param displayAttributes  显示属性
     */
    @Override
    public void saveOrUpdate(List<DisplayAttribute> displayAttributes) {
        //2.遍历参数数据
        DisplayAttribute displayAttributeFlag;
        Map<String, Object> params ;
        for (DisplayAttribute displayAttribute : displayAttributes) {
            params = new HashedMap();
            params.put("code", displayAttribute.getMapFieldColumnCode());
            MapFieldColumn mapFieldColumn = mapFieldColumnDAO.load(params);
            if(mapFieldColumn == null) {
                continue;
            }
            displayAttributeFlag = displayAttributeDAO.loadByMapFieldColumnCode(displayAttribute.getMapFieldColumnCode());
            if(displayAttributeFlag == null) {
                displayAttribute.setMapClassTableCode(mapFieldColumn.getMapClassTableCode());
                displayAttributeDAO.insert(displayAttribute);
            } else {
                displayAttributeDAO.update(displayAttribute);
            }
        }
    }


}