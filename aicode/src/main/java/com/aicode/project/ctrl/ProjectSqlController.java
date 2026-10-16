/*
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.project.ctrl;


import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.project.entity.ProjectSql;
import com.aicode.project.service.ProjectSqlService;
import com.aicode.project.vo.ProjectSqlVO;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目sql管理控制器
 * 1.查询一个详情信息
 * 2.查询项目sql信息集合
 * 3.创建项目sql
 * 4.修改项目sql
 * 5.删除项目sql
 *
 * @author hegaoye
 */
@RestController
@RequestMapping("/project/sql")
@Slf4j
@Tag(name = "项目sql脚本控制器", description = "项目sql脚本控制器")
public class ProjectSqlController {
    @Autowired
    private ProjectSqlService projectSqlService;


    @Operation(summary = "创建ProjectSql", description = "创建ProjectSql")
    @Parameters({
            @Parameter(name = "projectCode", description = "项目编码", required = true),
            @Parameter(name = "tsql", description = "sql脚本", required = true)
    })
    @PostMapping("/build")
    public R<ProjectSqlVO> build(@RequestParam String projectCode, @RequestParam String tsql) {
        ProjectSql newProjectSql = new ProjectSql();
        newProjectSql.setProjectCode(projectCode);
        newProjectSql.setTsql(tsql);
        projectSqlService.save(newProjectSql);
        // 写侧返回 VO 而非实体 —— 见 api-data-object 的
        // 「Controller SHALL 返回 VO 而非持久化实体」。4 字段投影，
        // 刻意排除自增主键 id（对外标识是雪花业务编码 code）。
        return R.success(toSqlVo(newProjectSql));
    }


    @Operation(summary = "查询一个详情信息", description = "查询一个详情信息")
    @Parameters({
            @Parameter(name = "code", description = "sql编码"),
            @Parameter(name = "projectCode", description = "项目编码")
    })
    @GetMapping(value = "/load")
    public R<ProjectSql> load(String code, String projectCode) {
        Assert.hasText(code, BaseException.BaseExceptionEnum.Empty_Param.toString());
        ProjectSql projectSql = projectSqlService.getOne(new LambdaQueryWrapper<ProjectSql>()
                .eq(ProjectSql::getCode, code)
                .eq(ProjectSql::getProjectCode, projectCode));
        log.info(JSON.toJSONString(projectSql));
        return R.success(projectSql);

    }


    @Operation(summary = "修改ProjectSql", description = "修改ProjectSql")
    @Parameters({
            @Parameter(name = "code", description = "tsql编码", required = true),
            @Parameter(name = "tsql", description = "sql脚本", required = true)
    })
    @PostMapping("/modify")
    public R<ProjectSqlVO> modify(ProjectSqlVO projectSqlVO) {
        Assert.hasText(projectSqlVO.getCode(), BaseException.BaseExceptionEnum.Empty_Param.toString());
        ProjectSql projectSqlLoad = projectSqlService.getOne(new LambdaQueryWrapper<ProjectSql>()
                .eq(ProjectSql::getCode, projectSqlVO.getCode()));
        if (null != projectSqlLoad) {
            projectSqlService.remove(new LambdaQueryWrapper<ProjectSql>().eq(ProjectSql::getCode, projectSqlVO.getCode()));
            projectSqlLoad.setTsql(projectSqlVO.getTsql());
            projectSqlService.save(projectSqlLoad);
            // 从持久化对象装配而非直返入参：入参只带 code + tsql 两个字段，
            // 直返会让响应缺 projectCode / state —— 前端无法确认写入结果。
            return R.success(toSqlVo(projectSqlLoad));
        } else {
            return R.failed(BaseException.BaseExceptionEnum.Server_Error);
        }
    }

    /**
     * 把持久化实体 {@link ProjectSql} 转换为写侧出参视图对象 {@link ProjectSqlVO}。
     *
     * 4 字段（code / projectCode / tsql / state）—— 刻意排除自增主键
     * {@code id}：按 `api-data-object` 的约定，数据库自增主键不出现在任何
     * 接口响应中，对外标识是雪花业务编码 {@code code}。
     *
     * 逐字段显式赋值而非 {@code BeanUtils.copyProperties} —— 与同包
     * {@code ProjectController#toSummary} / {@code copyToDetail} 保持一致：
     * 显式胜过隐式，字段重命名会在编译期暴露而非悄悄漂移。
     */
    private ProjectSqlVO toSqlVo(ProjectSql entity) {
        ProjectSqlVO vo = new ProjectSqlVO();
        vo.setCode(entity.getCode());
        vo.setProjectCode(entity.getProjectCode());
        vo.setTsql(entity.getTsql());
        vo.setState(entity.getState());
        return vo;
    }

}
