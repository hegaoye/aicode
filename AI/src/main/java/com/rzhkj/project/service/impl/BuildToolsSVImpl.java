/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.project.service.impl;

import com.aixin.core.base.BaseMybatisDAO;
import com.aixin.core.base.BaseMybatisSVImpl;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import com.baidu.fsg.uid.UidGenerator;

import com.ponddy.tutor.dao.BuildToolsDAO;
import com.ponddy.tutor.entity.BuildTools;


@Component
@Service
public class BuildToolsSVImpl extends BaseMybatisSVImpl<BuildTools,Long> implements BuildToolsSV{

	@Resource
	private BuildToolsDAO buildToolsDAO;

	@Resource
	private UidGenerator uidGenerator;

	@Override
	protected BaseMybatisDAO getBaseMybatisDAO() {
		return buildToolsDAO;
	}

}
