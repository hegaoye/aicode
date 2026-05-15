/*
 * Powered By [lixin]
 *
 */

package com.aicode.project.service;


import cn.hutool.core.date.DateUtil;
import com.aicode.account.dao.mapper.AccountMapper;
import com.aicode.account.entity.Account;
import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.core.enums.SuffixTypeEnum;
import com.aicode.core.tools.FileUtil;
import com.aicode.core.tools.StringTools;
import com.aicode.project.dao.mapper.ProjectMapper;
import com.aicode.project.entity.Project;
import com.aicode.setting.entity.SettingKey;
import com.aicode.setting.service.SettingService;
import com.alibaba.fastjson2.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class LogsSVImpl implements LogsSV {

    @Resource
    private SettingService settingService;
    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private AccountMapper accountMapper;

    /**
     * 创建日志文件
     * 1.根据项目编码查询出用户和项目信息
     * 2.用户名+项目名生成唯一的文件夹
     * 3.根据时间戳生成文件
     * 4.返回文件路径
     *
     * @param projectCode 项目编码
     * @param date        时间
     * @return String
     */
    @Override
    public String createLogFiles(String projectCode, Date date) {
        String filePath = null;
        try {
            //1.根据项目编码查询出用户和项目信息
            Project project = projectMapper.selectOne(new LambdaQueryWrapper<Project>().eq(Project::getCode, projectCode));

            log.info("项目编码查询,project:{}", project);
            if (project == null) {
                return null;
            }

            String accountCode = project.getAccountCode();
            Account account = accountMapper.selectOne(new LambdaQueryWrapper<Account>().eq(Account::getCode, accountCode));
            log.info("用户查询,account:{}", account);
            String accountName = account.getAccount();
            String name = project.getEnglishName();
            //2.用户名+项目名生成唯一的文件夹
            String fileName = accountName + "_" + name + "_logs";
            String workspace = settingService.load(SettingKey.Workspace);
            log.info("workspace:{}", workspace);
            String path = workspace + fileName;
            FileUtil.createDir(path, null);
            //3.根据时间戳生成文件
            String dateStr = DateUtils.format(date, "yyyyMMddHHmmss");
            String logName = dateStr + SuffixTypeEnum.Log.val;
            FileUtil.createDir(path, logName);
            //文件路径
            filePath = fileName + "/" + logName;
            log.info("filePath:{}", filePath);
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
        }

        //4.返回文件路径
        return filePath;
    }

    /**
     * 保存日志数据
     *
     * @param logs 日志
     * @param path 日志路径
     * @return String
     */
    @Override
    public boolean saveLogs(String logs, String path) {
        log.info("logs:{} path:{}", logs, path);

        //参数为空判断
        if (StringTools.isEmpty(logs)) {
            log.warn("logs is empty");
            return false;
        }
        if (StringTools.isEmpty(path)) {
            log.warn("path is empty");
            return false;
        }
        String workspace = settingService.load(SettingKey.Workspace);
        path = workspace + path;
        FileOutputStream fop;
        File file;
        try {
            file = new File(path);
            fop = new FileOutputStream(file, true);
            OutputStreamWriter oStreamWriter = new OutputStreamWriter(fop, "utf-8");
            BufferedWriter writer = new BufferedWriter(oStreamWriter);
            writer.append(logs);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据项目编码查询项目路径
     *
     * @param projectCode 项目编码
     * @return String
     */
    @Override
    public String loadFilePath(String projectCode) {
        Project project = projectMapper.selectOne(new LambdaQueryWrapper<Project>().eq(Project::getCode, projectCode));
        if (project == null) {
            return null;
        }
        String accountCode = project.getAccountCode();
        Account account = accountMapper.selectOne(new LambdaQueryWrapper<Account>().eq(Account::getCode, accountCode));
        String accountName = account.getAccount();
        String name = project.getEnglishName();
        //2.用户名+项目名生成唯一的文件夹
        String fileName = accountName + "_" + name + "_logs";
        return fileName;
    }

    /**
     * 查询日志信息
     *
     * @param projectCode 文件路径
     * @param datetime    构建时间
     * @return String
     */
    @Override
    public R scanPath(String projectCode, String datetime) {
        log.info("projectCode:{} datetime:{}", projectCode, datetime);
        String name = this.loadFilePath(projectCode);
        String filePath = null;
        try {
            String date = DateUtil.format(DateUtil.parse(datetime), "yyyyMMddHHmmss");
            if (StringTools.isNotEmpty(date)) {
                filePath = name + "/" + date + SuffixTypeEnum.Log.val;
            }
            String workspace = settingService.load(SettingKey.Workspace);
            String path = workspace;
            if (StringTools.isNotEmpty(filePath)) {
                path = path + filePath;
            }
            File file = new File(path);
            if (file.isDirectory()) {
                List<Map<String, String>> mapList = FileUtil.sanDirFiles(path, filePath);
                return R.success(mapList);
            }
            if (file.isFile() && !filePath.contains(".jar")) {
                try {
                    String fileStr = FileUtils.readFileToString(new File(path), "UTF-8");
                    return R.success(fileStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return R.success(filePath.replaceAll("/\\w*\\.jar", ""));
    }


}
