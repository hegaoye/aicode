


package ${basePackage}.setting.service;

import ${basePackage}.core.base.BaseMybatisSV;
import ${basePackage}.setting.entity.Setting;

import java.util.List;

public interface SettingSV extends BaseMybatisSV<Setting, Long> {


    /**
     * 根据key 获得 value
     *
     * @param key Setting.Key
     * @return T
     */
    <T> T load(Setting.Key key, Class<T>... clazz);

    /**
     * 修改设置值
     *
     * @param setting 设置对象
     */
    void modifySetting(Setting setting);

    /**
     * 查询所有设置接口
     *
     * @return List<Setting>
     */
    List<Setting> listSetting();
}
