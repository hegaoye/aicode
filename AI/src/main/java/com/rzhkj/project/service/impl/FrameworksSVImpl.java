/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.core.enums.YNEnum;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.exceptions.FrameworksException;
import com.rzhkj.core.tools.StringTools;
import com.rzhkj.project.dao.FrameworksDAO;
import com.rzhkj.project.entity.Frameworks;
import com.rzhkj.project.service.FrameworksSV;
import com.rzhkj.setting.dao.SettingDAO;
import com.rzhkj.setting.entity.Setting;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class FrameworksSVImpl extends BaseMybatisSVImpl<Frameworks, Long> implements FrameworksSV {

    @Resource
    private FrameworksDAO frameworksDAO;

    @Resource
    private SettingDAO settingDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return frameworksDAO;
    }

    /**
     * 保存技术框架
     * 如果发现githome为null则自动设置为系统默认的模板仓库
     * @param entity 实体
     * @throws BaseException
     */
    @Override
    public void save(Frameworks entity) throws BaseException {
        if (entity == null || StringTools.isEmpty(entity.getDescription()) || StringTools.isEmpty(entity.getName())) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new FrameworksException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        entity.setCode(String.valueOf(uidGenerator.getUID()));
        if (entity.getGitHome() == null) {//默认走系统设置
            Setting setting = settingDAO.loadByKey(Setting.Key.GitHome_Default.name());
            entity.setGitHome(setting.getV());
            entity.setIsPublic(YNEnum.Y.name());
        }
        super.save(entity);
    }

    /**
     * 删除
     *
     * @param code 技术编码
     */
    @Override
    public void delete(String code) {
        frameworksDAO.delete(code);
    }
}
