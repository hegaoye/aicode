/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于龐帝業務系统.
 *
 */


package ${basePackage}.setting.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Maps;
import ${basePackage}.core.base.BaseMybatisDAO;
import ${basePackage}.core.base.BaseMybatisSVImpl;
import ${basePackage}.setting.service.SettingSV;
import ${basePackage}.core.exceptions.BaseException;
import ${basePackage}.core.exceptions.SettingException;
import ${basePackage}.core.tools.JSON;
import ${basePackage}.setting.dao.SettingDAO;
import ${basePackage}.setting.entity.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Component
@Service
public class SettingSVImpl extends BaseMybatisSVImpl<Setting, Long> implements SettingSV {
    private final static Logger logger = LoggerFactory.getLogger(SettingSVImpl.class);

    @Resource
    private SettingDAO settingDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return settingDAO;
    }


    /**
     * 根据key 获得 value
     *
     * @param key Setting.Key
     * @return T
     */
    @Override
    public <T> T load(Setting.Key key, Class<T>... clazz) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("k", key.name());
        Setting setting = this.load(map);
        if (clazz == null || clazz.length <= 0 || clazz[0].equals(String.class)) {
            T t = toStr(setting.getV(), (Class<T>) String.class);
            logger.info(" setting 获取 key : " + t.toString());
            return t;
        }
        if (clazz[0].equals(Integer.class)) {
            T t = clazz[0].cast(Integer.parseInt(setting.getV()));
            logger.info(" setting 获取 key : " + t.toString());
            return t;
        }
        return null;
    }

    private <T> T toStr(String value, Class<T> clazz) {
        return clazz.cast((String.valueOf(value)));
    }


    /**
     * 修改设置值
     * 1.判断是否存在
     * 2.修改设置值
     *
     * @param setting 设置对象
     */
    @Override
    public void modifySetting(Setting setting) {
        //1.判断是否存在
        if (setting == null)  {
            throw new SettingException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        Setting settingLoad = settingDAO.loadByKey(setting.getK());
        if (settingLoad == null)  {
            throw new SettingException(BaseException.BaseExceptionEnum.Result_Not_Exist);
        }
        //2.修改设置值
        logger.info(" setting 修改设置值 : " + JSON.toJSONString(setting));
        settingDAO.modifySetting(setting);
    }

    /**
     * 查询所有设置接口
     *
     * @return List<Setting>
     */
    @Override
    public List<Setting> listSetting() {
        List<Setting> settingList = settingDAO.query(null);
        logger.info(" setting 修改设置值 : " + JSON.toJSONString(settingList));
        return settingList;
    }


}
