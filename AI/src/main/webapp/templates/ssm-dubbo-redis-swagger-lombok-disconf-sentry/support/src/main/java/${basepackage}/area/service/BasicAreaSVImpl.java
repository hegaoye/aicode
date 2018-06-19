/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *       郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *       代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *       本代码仅用于龐帝業務系统.目.
 */


package ${basePackage}.area.service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.baidu.fsg.uid.UidGenerator;
import ${basePackage}.area.dao.BasicAreaDAO;
import ${basePackage}.area.entity.BasicArea;
import ${basePackage}.core.base.BaseMybatisDAO;
import ${basePackage}.area.service.BasicAreaSV;
import ${basePackage}.core.base.BaseMybatisSVImpl;
import ${basePackage}.core.exceptions.BaseException;
import ${basePackage}.core.exceptions.BasicAreaException;
import ${basePackage}.core.tools.redis.RedisKey;
import ${basePackage}.core.tools.redis.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Service
public class BasicAreaSVImpl extends BaseMybatisSVImpl<BasicArea, String> implements BasicAreaSV {
    private final static Logger logger = LoggerFactory.getLogger(BasicAreaSVImpl.class);

    @Resource
    private BasicAreaDAO basicAreaDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Resource
    private RedisUtils redisUtils;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return basicAreaDAO;
    }


    /**
     * 根据areaCode获得区域对象
     *
     * @param areaCode 行政区域代码
     * @return BasicArea
     */
    @Override
    public BasicArea load(String areaCode) {
        if (StringUtils.isBlank(areaCode)) {
            logger.error("根据areaCode获得区域对象失败，原因：" + BaseException.BaseExceptionEnum.Result_Not_Exist.error + ",areaCode:" + areaCode);
            throw new BasicAreaException(BaseException.BaseExceptionEnum.Empty_Param, areaCode);
        }

        BasicArea basicArea = null;
        String areaCacheKey = RedisKey.genAreaKey(areaCode);
        if (!redisUtils.hasKey(areaCacheKey)) {
            String province = areaCode.substring(0, 2);
            BasicArea basicAreaProvince = basicAreaDAO.loadProvince(province);
            List<BasicArea> basicAreaCitys = basicAreaDAO.loadCity(province);
            List<BasicArea> basicAreaAreas = basicAreaDAO.loadArea(province);
            for (BasicArea basicAreaObj : basicAreaCitys) {
                List<BasicArea> basicAreaAreaTemp = new ArrayList<>();
                for (BasicArea basicAreaArea : basicAreaAreas) {
                    if (basicAreaObj.getCity().equals(basicAreaArea.getCity())) {
                        basicAreaAreaTemp.add(basicAreaArea);
                    }
                }
                basicAreaObj.setChildren(basicAreaAreaTemp);
            }
            basicAreaProvince.setChildren(basicAreaCitys);
            redisUtils.set(areaCacheKey, JSON.toJSONString(basicAreaProvince));
            basicArea = basicAreaProvince;
        } else {
            String areaJson = redisUtils.get(areaCacheKey).toString();
            basicArea = JSON.parseObject(areaJson, BasicArea.class);
        }

        if (basicArea == null) {
            logger.error("根据areaCode获得区域对象失败，原因：" + BaseException.BaseExceptionEnum.Result_Not_Exist.error + ",areaCode:" + areaCode);
            throw new BasicAreaException(BaseException.BaseExceptionEnum.Empty_Param, areaCode);
        }

        return basicArea;
    }

    /**
     * 获取所有省份
     *
     * @return List<BasicArea>
     */
    @Override
    public List<BasicArea> listProvoice() {
        String provoiceCacheKey = RedisKey.genAreaKey("0");
        List<BasicArea> provoices = null;
        if (!redisUtils.hasKey(provoiceCacheKey)) {
            provoices = basicAreaDAO.listProvoice();
            redisUtils.set(provoiceCacheKey, JSON.toJSONString(provoices));
        } else {
            String provoiceJson = redisUtils.get(provoiceCacheKey).toString();
            provoices = JSON.parseArray(provoiceJson, BasicArea.class);
        }
        return provoices;
    }

    /**
     * 加载所有区域对象至缓存
     *
     * @return
     */
    @Override
    public boolean queryAll2Redis() {
        List<BasicArea> list = basicAreaDAO.queryAll();
        Map<String, BasicArea> map = new HashMap<>();

        for (BasicArea basicArea : list) {
            map.put(basicArea.getAreaCode(), basicArea);
        }

        if (list != null && list.size() > 0) {
            redisUtils.set("area_cache", JSON.toJSONString(map));
            return true;
        }
        return false;
    }
}