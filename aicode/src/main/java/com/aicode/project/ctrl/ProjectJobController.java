/*
 * aicode
 */
package com.aicode.project.ctrl;

import com.aicode.core.BaseException;
import com.aicode.core.R;
import com.aicode.project.entity.ProjectJob;
import com.aicode.project.service.ProjectJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务
 *
 * @author aicode
 */
@RestController
@RequestMapping("/project/job")
@Slf4j
@Tag(name = "任务控制器", description = "任务控制器")
public class ProjectJobController {
    @Autowired
    private ProjectJobService projectJobService;


    @Operation(summary = "执行任务", description = "执行任务")
    @Parameters({
            @Parameter(name = "code", description = "项目编码")
    })
    @GetMapping(value = "/execute")
    public R execute(@RequestParam("code") String projectCode) {
        try {
            log.info("执行任务 , projectCode:{}", projectCode);
            Assert.hasText(projectCode, BaseException.BaseExceptionEnum.Empty_Param.toString());
            //            Session webSocketSession = WSClientManager.get();
            //            if (webSocketSession != null) {
            //生成代码
            ProjectJob projectJob = projectJobService.execute(projectCode);
            return R.success(projectJob);
            //            }
        } catch (Exception e) {
            log.error("异常", e);
            log.error(e.getMessage());
            return R.failed(BaseException.BaseExceptionEnum.Server_Error);
        }
    }

}
