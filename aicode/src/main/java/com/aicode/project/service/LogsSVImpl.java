/*
 * Powered By [lixin]
 *
 */

package com.aicode.project.service;


import com.aicode.account.dao.AccountDAO;
import com.aicode.account.entity.Account;
import com.aicode.core.entity.R;
import com.aicode.core.enums.SuffixTypeEnum;
import com.aicode.core.exceptions.BaseException;
import com.aicode.core.tools.DateTools;
import com.aicode.core.tools.FileUtil;
import com.aicode.core.tools.StringTools;
import com.aicode.project.dao.ProjectDAO;
import com.aicode.project.entity.Project;
import com.aicode.setting.entity.SettingKey;
import com.aicode.setting.service.SettingService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Component
@Service
public class LogsSVImpl implements LogsSV {

    @Resource
    private SettingService settingService;

    @Resource
    private ProjectDAO projectDAO;

    @Resource
    private AccountDAO accountDAO;

    /**
     * 创建日志文件
     * 1.根据项目编码查询出用户和项目信息
     * 2.用户名+项目名生成唯一的文件夹
     * 3.根据时间戳生成文件
     * 4.返回文件路径
     *
     * @param projectCode 项目编码
     * @param date
     * @return String
     */
    @Override
    public String createLogFiles(String projectCode, Date date) {
        String filePath = null;
        try {
            //1.根据项目编码查询出用户和项目信息
            Project project = projectDAO.selectOne(new LambdaQueryWrapper<Project>()
                    .eq(Project::getCode, projectCode));
            if (project == null) {
                return null;
            }
            String accountCode = project.getAccountCode();
            Account account = accountDAO.selectOne(new LambdaQueryWrapper<Account>().eq(Account::getCode, accountCode));
            String accountName = account.getAccount();
            String name = project.getName();
            //2.用户名+项目名生成唯一的文件夹
            String fileName = accountName + "_" + name + "_logs";
            String workspace = settingService.load(SettingKey.Workspace);
            String path = workspace + fileName;
            FileUtil.createDir(path, null);
            //3.根据时间戳生成文件
            String dateStr = DateTools.dateToNum14(date);
            String logName = dateStr + SuffixTypeEnum.Log.val;
            FileUtil.createDir(path, logName);
            //文件路径
            filePath = fileName + "/" + logName;
        } catch (Exception e) {
            e.printStackTrace();
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
        //参数为空判断
        if (StringTools.isEmpty(logs)) {
            throw new BaseException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        if (StringTools.isEmpty(path)) {
            throw new BaseException(BaseException.BaseExceptionEnum.Empty_Param);
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
        Project project = projectDAO.selectOne(new LambdaQueryWrapper<Project>().eq(Project::getCode, projectCode));
        if (project == null) {
            return null;
        }
        String accountCode = project.getAccountCode();
        Account account = accountDAO.selectOne(new LambdaQueryWrapper<Account>().eq(Account::getCode, accountCode));
        String accountName = account.getAccount();
        String name = project.getName();
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
        String name = this.loadFilePath(projectCode);
        String filePath = null;
        try {
            String date = DateTools.stringToNum14(datetime, "yyyy-MM-dd HH:mm:ss");
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
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return R.success(filePath.replaceAll("/\\w*\\.jar", ""));
    }


}
