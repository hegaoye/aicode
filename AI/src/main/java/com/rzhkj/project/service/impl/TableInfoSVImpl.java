/*
 * Powered By [lixin]
 *
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.TableInfoDAO;
import com.rzhkj.project.entity.TableInfo;
import com.rzhkj.project.service.TableInfoSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class TableInfoSVImpl extends BaseMybatisSVImpl<TableInfo, Long> implements TableInfoSV {

    @Resource
    private TableInfoDAO tableInfoDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return tableInfoDAO;
    }

}
