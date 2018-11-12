/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import io.aicode.core.base.BaseMybatisDAO;
import io.aicode.core.base.BaseMybatisSVImpl;
import io.aicode.core.enums.YNEnum;
import io.aicode.core.exceptions.BaseException;
import io.aicode.core.exceptions.FrameworksException;
import io.aicode.core.tools.StringTools;
import io.aicode.project.dao.FrameworksDAO;
import io.aicode.project.entity.Frameworks;
import io.aicode.project.service.FrameworksSV;
import io.aicode.setting.dao.SettingDAO;
import io.aicode.setting.entity.Setting;
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
