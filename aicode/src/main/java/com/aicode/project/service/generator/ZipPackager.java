package com.aicode.project.service.generator;

import com.aicode.core.tools.ZipTools;
import com.aicode.project.dao.mapper.ProjectMapper;
import com.aicode.project.entity.Project;
import com.aicode.setting.dao.mapper.SettingMapper;
import com.aicode.setting.entity.Setting;
import com.aicode.setting.entity.SettingKey;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * ZIP 打包器：把工作空间目录打成 {@code <englishName>.zip} 放到
 * {@code Setting(Repository_Path)} 指定的目录，并回写 {@code project.downloadUrl}。
 */
@Slf4j
@Component
public class ZipPackager {

    @Autowired
    private SettingMapper settingMapper;

    @Autowired
    private ProjectMapper projectMapper;

    /**
     * 把 {@code <Setting.Workspace>/<englishName>} 打成 zip 放到 {@code <Setting.Repository_Path>/<englishName>.zip}。
     *
     * @return 写出的 zip 完整路径（{@code <Repository_Path>/<englishName>.zip}）
     */
    public String pack(Project project) {
        String englishName = project.getEnglishName();
        String workspaceRoot = loadSetting(SettingKey.Workspace.name());
        String repositoryRoot = loadSetting(SettingKey.Repository_Path.name());
        String projectWorkspacePath = workspaceRoot + "/" + englishName;
        String zipBasePath = repositoryRoot + "/" + englishName;
        String zipPath = zipBasePath + ".zip";

        ensureRepositoryDir(repositoryRoot);
        deleteIfExists(zipPath);
        ZipTools.zip(zipBasePath, projectWorkspacePath);

        project.setDownloadUrl("/project/download/" + englishName);
        projectMapper.update(project, new LambdaQueryWrapper<Project>().eq(Project::getCode, project.getCode()));
        return zipPath;
    }

    private String loadSetting(String key) {
        Setting setting = settingMapper.selectOne(new LambdaQueryWrapper<Setting>().eq(Setting::getK, key));
        if (setting == null || setting.getV() == null) {
            throw new IllegalStateException("缺失必要设置: " + key);
        }
        return setting.getV();
    }

    private void ensureRepositoryDir(String repositoryRoot) {
        File dir = new File(repositoryRoot);
        if (!dir.exists() && !dir.mkdirs()) {
            log.warn("创建 Repository 目录失败: {}", repositoryRoot);
        }
    }

    private void deleteIfExists(String path) {
        File f = new File(path);
        if (f.exists() && !f.delete()) {
            log.warn("删除旧 zip 失败: {}", path);
        }
    }
}
