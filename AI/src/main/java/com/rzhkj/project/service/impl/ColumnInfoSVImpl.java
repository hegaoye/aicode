/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ColumnInfoDAO;
import com.rzhkj.project.entity.ColumnInfo;
import com.rzhkj.project.service.ColumnInfoSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class ColumnInfoSVImpl extends BaseMybatisSVImpl<ColumnInfo, Long> implements ColumnInfoSV {

    @Resource
    private ColumnInfoDAO columnInfoDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return columnInfoDAO;
    }

}
