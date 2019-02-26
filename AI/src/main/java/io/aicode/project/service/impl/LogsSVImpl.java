/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import io.aicode.core.entity.BeanRet;
import io.aicode.core.enums.SuffixTypeEnum;
import io.aicode.core.exceptions.BaseException;
import io.aicode.core.tools.DateTools;
import io.aicode.core.tools.FileUtil;
import io.aicode.core.tools.HandleFuncs;
import io.aicode.core.tools.StringTools;
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
import java.util.Date;
import java.util.List;
import java.util.Map;


@Component
@Service
public class LogsSVImpl implements LogsSV {

    @Resource
    private UidGenerator uidGenerator;

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
     * @return String
     */
    @Override
    public String createLogFiles(String projectCode) {
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
            String fileName = accountName + "-" + name;
            String workspace = settingSV.load(Setting.Key.SandBox_Path);
            String path = new HandleFuncs().getCurrentClassPath() + workspace + fileName;
            FileUtil.createDir(path, null);
            //3.根据时间戳生成文件
            String date = DateTools.dateToNum14(new Date());
            String logName = date + SuffixTypeEnum.Log.val;
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
     * @param logs    日志
     * @param path    日志路径
     * @param isClose 是否关闭连接
     * @return String
     */
    @Override
    public boolean saveLogs(String logs, String path, Boolean isClose) {
        //参数为空判断
        if (StringTools.isEmpty(logs)) {
            throw new BaseException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        if (StringTools.isEmpty(path)) {
            throw new BaseException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        if (isClose == null) {
            throw new BaseException(BaseException.BaseExceptionEnum.Empty_Param);
        }
        String workspace = settingSV.load(Setting.Key.SandBox_Path);
        path = new HandleFuncs().getCurrentClassPath() + workspace + path;
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
        String fileName = accountName + "-" + name;
        return  fileName;
    }

    @Override
    public BeanRet scanPath(String filePath) {
        String workspace = settingSV.load(Setting.Key.SandBox_Path);
        String path = new HandleFuncs().getCurrentClassPath() + workspace;
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
        return BeanRet.create(true, "", filePath.replaceAll("/\\w*\\.jar", ""));
    }
}
