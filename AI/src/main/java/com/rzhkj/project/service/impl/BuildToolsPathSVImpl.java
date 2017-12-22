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

import com.ponddy.tutor.dao.BuildToolsPathDAO;
import com.ponddy.tutor.entity.BuildToolsPath;


@Component
@Service
public class BuildToolsPathSVImpl extends BaseMybatisSVImpl<BuildToolsPath,Long> implements BuildToolsPathSV{

	@Resource
	private BuildToolsPathDAO buildToolsPathDAO;

	@Resource
	private UidGenerator uidGenerator;

	@Override
	protected BaseMybatisDAO getBaseMybatisDAO() {
		return buildToolsPathDAO;
	}

}
