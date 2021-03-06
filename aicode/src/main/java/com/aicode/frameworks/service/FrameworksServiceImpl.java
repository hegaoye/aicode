/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.frameworks.service;

import com.aicode.core.enums.YNEnum;
import com.aicode.setting.dao.SettingDAO;
import com.aicode.setting.entity.Setting;
import com.aicode.setting.entity.SettingKey;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aicode.frameworks.dao.FrameworksDAO;
import com.aicode.frameworks.dao.mapper.FrameworksMapper;
import com.aicode.frameworks.entity.Frameworks;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Date;


/**
 * 框架技术池
 *
 * @author hegaoye
 */
@Slf4j
@Service
public class FrameworksServiceImpl extends ServiceImpl<FrameworksMapper, Frameworks> implements FrameworksService {

    @Autowired
    private FrameworksDAO frameworksDAO;

    @Autowired
    private SettingDAO settingDAO;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    public boolean save(Frameworks entity) {
        entity.setCode(String.valueOf(uidGenerator.getUID()));
        if (entity.getGitHome() == null) {//默认走系统设置
            Setting setting = settingDAO.selectOne(new LambdaQueryWrapper<Setting>()
                    .eq(Setting::getK, SettingKey.GitHome_Default.name()));
            entity.setGitHome(setting.getV());
            entity.setIsPublic(YNEnum.Y.name());
        }

        return super.save(entity);
    }

    /**
     * 分页查询 投注项水位
     *
     * @param queryWrapper 查询条件
     * @param offset       起始行
     * @param limit        步长
     * @return List<Frameworks>
     */
    @Override
    public List<Frameworks> list(QueryWrapper<Frameworks> queryWrapper, int offset, int limit) {
        return frameworksDAO.list(queryWrapper, offset, limit);
    }
}


