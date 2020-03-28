/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service.impl;


import io.aicode.base.core.BeanRet;
import io.aicode.base.enums.SuffixTypeEnum;
import io.aicode.base.exceptions.BaseException;
import io.aicode.base.tools.DateTools;
import io.aicode.base.tools.FileUtil;
import io.aicode.base.tools.StringTools;
import io.aicode.project.dao.AccountDAO;
import io.aicode.project.dao.ProjectDAO;
import io.aicode.project.entity.Account;
import io.aicode.project.entity.Project;
import io.aicode.project.service.LogsSV;
import io.aicode.setting.entity.Setting;
import io.aicode.setting.service.SettingSV;
import org.apache.commons.collections.map.HashedMap;
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
    private SettingSV settingSV;

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
            Map<String, Object> params = new HashedMap();
            params.put("code", projectCode);
            Project project = projectDAO.load(params);
            if (project == null) {
                return null;
            }
            String accountCode = project.getAccountCode();
            params = new HashedMap();
            params.put("code", accountCode);
            Account account = accountDAO.load(params);
            String accountName = account.getAccount();
            String name = project.getName();
            //2.用户名+项目名生成唯一的文件夹
            String fileName = accountName + "_" + name + "_logs";
            String workspace = settingSV.load(Setting.Key.Workspace);
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
        String workspace = settingSV.load(Setting.Key.Workspace);
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
    public String loadFilePath(String projectCode) {
        Map<String, Object> params = new HashedMap();
        params.put("code", projectCode);
        Project project = projectDAO.load(params);
        if (project == null) {
            return null;
        }
        String accountCode = project.getAccountCode();
        params = new HashedMap();
        params.put("code", accountCode);
        Account account = accountDAO.load(params);
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
    public BeanRet scanPath(String projectCode, String datetime) {
        String name = this.loadFilePath(projectCode);
        String filePath = null;
        try {
            String date = DateTools.stringToNum14(datetime, "yyyy-MM-dd HH:mm:ss");
            if (StringTools.isNotEmpty(date)) {
                filePath = name + "/" + date + SuffixTypeEnum.Log.val;
            }
            String workspace = settingSV.load(Setting.Key.Workspace);
            String path = workspace;
            if (StringTools.isNotEmpty(filePath)) {
                path = path + filePath;
            }
            File file = new File(path);
            if (file.isDirectory()) {
                List<Map<String, String>> mapList = FileUtil.sanDirFiles(path, filePath);
                return BeanRet.create(true, "成功", mapList);
            }
            if (file.isFile() && !filePath.contains(".jar")) {
                try {
                    String fileStr = FileUtils.readFileToString(new File(path), "UTF-8");
                    return BeanRet.create(true, "成功", fileStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return BeanRet.create(true, "", filePath.replaceAll("/\\w*\\.jar", ""));
    }
}
