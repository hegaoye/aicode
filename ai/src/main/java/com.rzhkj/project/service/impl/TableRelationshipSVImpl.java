/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.TableRelationshipDAO;
import com.rzhkj.project.entity.TableRelationship;
import com.rzhkj.project.service.TableRelationshipSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Component
@Service
public class TableRelationshipSVImpl extends BaseMybatisSVImpl<TableRelationship, Long> implements TableRelationshipSV {

    @Resource
    private TableRelationshipDAO tableRelationshipDAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return tableRelationshipDAO;
    }

}
