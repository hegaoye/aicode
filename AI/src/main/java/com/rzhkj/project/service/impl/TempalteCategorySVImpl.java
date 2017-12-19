/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.base.core.StringHelper;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.core.exceptions.BaseException;
import com.rzhkj.core.exceptions.ProjectException;
import com.rzhkj.core.exceptions.TemplateCategoryException;
import com.rzhkj.core.tools.StringTools;
import com.rzhkj.project.dao.TempalteCategoryDAO;
import com.rzhkj.project.entity.TempalteCategory;
import com.rzhkj.project.service.TempalteCategorySV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class TempalteCategorySVImpl extends BaseMybatisSVImpl<TempalteCategory, Long> implements TempalteCategorySV {

    @Resource
    private TempalteCategoryDAO tempalteCategoryDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return tempalteCategoryDAO;
    }

    @Override
    public void save(TempalteCategory entity) throws BaseException {
        if (entity == null || StringTools.isEmpty(entity.getDescription()) || StringTools.isEmpty(entity.getName())) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new TemplateCategoryException(BaseException.BaseExceptionEnum.Empty_Param);
        }

        entity.setCode(String.valueOf(uidGenerator.getUID()));
        super.save(entity);
    }

    /**
     * 删除
     *
     * @param code 类型编码
     */
    @Override
    public void delete(String code) {
        if (StringTools.isEmpty(code)) {
            logger.error(BaseException.BaseExceptionEnum.Empty_Param.toString());
            throw new TemplateCategoryException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        tempalteCategoryDAO.delete(code);

    }
}
