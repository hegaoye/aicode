/*
 *
 *                        http://www.aicode.io
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于AI-Code.
 *
 */


package io.aicode.setting.service;

import io.aicode.core.base.BaseMybatisSV;
import io.aicode.setting.entity.Setting;

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
