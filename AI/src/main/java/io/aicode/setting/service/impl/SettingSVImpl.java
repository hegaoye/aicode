/*
 *
 *                        http://www.aicode.io
 *
 *
 *        本代码仅用于AI-Code.
 *
 */


package io.aicode.setting.service.impl;

import com.alibaba.fastjson.JSON;
import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Maps;
import io.aicode.base.BaseMybatisDAO;
import io.aicode.base.BaseMybatisSVImpl;
import io.aicode.base.exceptions.BaseException;
import io.aicode.base.exceptions.SettingException;
import io.aicode.setting.dao.SettingDAO;
import io.aicode.setting.entity.Setting;
import io.aicode.setting.service.SettingSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
        if (setting == null) {
            throw new SettingException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        Setting settingLoad = settingDAO.loadByKey(setting.getK());
        if (settingLoad == null) {
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
