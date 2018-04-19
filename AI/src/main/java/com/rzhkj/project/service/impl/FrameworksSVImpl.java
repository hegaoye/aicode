/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.exceptions.FrameworksException;
import com.rzhkj.core.tools.StringTools;
import com.rzhkj.project.dao.FrameworksDAO;
import com.rzhkj.project.entity.Frameworks;
import com.rzhkj.project.entity.FrameworksStateEnum;
import com.rzhkj.project.service.FrameworksSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class FrameworksSVImpl extends BaseMybatisSVImpl<Frameworks, Long> implements FrameworksSV {

    @Resource
    private FrameworksDAO frameworksDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return frameworksDAO;
    }

    @Override
    public void save(Frameworks entity) throws BaseException {
        if (entity == null || StringTools.isEmpty(entity.getDescription()) || StringTools.isEmpty(entity.getName())) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new FrameworksException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        entity.setCode(String.valueOf(uidGenerator.getUID()));
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
