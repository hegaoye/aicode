/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.tools.StringTools;
import com.rzhkj.project.dao.FrameworkAttributeDAO;
import com.rzhkj.project.entity.FrameworkAttribute;
import com.rzhkj.project.service.FrameworkAttributeSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class FrameworkAttributeSVImpl extends BaseMybatisSVImpl<FrameworkAttribute, Long> implements FrameworkAttributeSV {

    @Resource
    private FrameworkAttributeDAO frameworkAttributeDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return frameworkAttributeDAO;
    }

    @Override
    public void save(FrameworkAttribute entity) throws BaseException {
        if (entity == null || StringTools.isEmpty(entity.getAttribute()) || StringTools.isEmpty(entity.getFrameworkCode())) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new BaseException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        entity.setCode(String.valueOf(uidGenerator.getUID()));
        super.save(entity);
    }

    /**
     * 删除
     *
     * @param code 属性编码
     */
    @Override
    public void delete(String code) {
        frameworkAttributeDAO.delete(code);
    }
}
