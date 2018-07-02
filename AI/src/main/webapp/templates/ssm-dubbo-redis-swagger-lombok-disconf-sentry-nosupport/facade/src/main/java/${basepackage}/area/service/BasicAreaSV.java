package ${basePackage}.area.service;

import ${basePackage}.area.entity.BasicArea;
import ${basePackage}.core.base.BaseMybatisSV;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 1.根据areaCode获得区域对象
 * 2.获取所有省份
 */
@Service("basicAreaSV")
public interface BasicAreaSV extends BaseMybatisSV<BasicArea, String> {

    /**
     * 根据areaCode获得区域对象
     *
     * @param areaCode 行政区域代码
     * @return BasicArea
     */
    BasicArea load(String areaCode);

    /**
     * 获取所有省份
     *
     * @return List<BasicArea>
     */
    List<BasicArea> listProvoice();

    /**
     * 加载所有区域对象至缓存
     *
     * @return
     */
    boolean queryAll2Redis();
}