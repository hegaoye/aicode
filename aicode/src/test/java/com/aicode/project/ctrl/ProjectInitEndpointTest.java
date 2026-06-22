package com.aicode.project.ctrl;

import com.aicode.exceptions.ProjectException;
import com.aicode.project.service.ProjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 覆盖 spec/project-init-endpoint 的关键契约：
 * - 正常 init 流程（code 非空）
 * - 空 code → Empty_Param（9004）
 * - 项目不存在 → Result_Not_Exist（9006）
 *
 * ProjectController.init 是薄包装；测试 ProjectService.execute 即可。
 * MockMvc 用独立构建（避免全局 Security/Filter 干扰）。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class ProjectInitEndpointTest {

    @Autowired
    private ProjectController projectController;

    @MockBean private ProjectService projectService;
    @MockBean private com.aicode.setting.service.SettingService settingService;
    @MockBean private com.aicode.project.service.ProjectFramworkService projectFramworkService;
    @MockBean private com.aicode.project.service.ProjectMapService projectMapService;
    @MockBean private com.aicode.project.service.ProjectModuleService projectModuleService;
    @MockBean private com.aicode.project.service.ProjectJobService projectJobService;
    @MockBean private com.aicode.frameworks.service.FrameworksService frameworksService;
    @MockBean private com.aicode.project.service.ProjectSqlService projectSqlService;
    @MockBean private com.aicode.project.service.ProjectRepositoryAccountService projectRepositoryAccountService;
    @MockBean private com.aicode.display.service.DisplayAttributeService displayAttributeService;
    @MockBean private com.aicode.map.service.MapRelationshipService mapRelationshipService;
    @MockBean private org.springframework.web.socket.server.standard.ServerEndpointExporter serverEndpointExporter;

    private MockMvc mockMvc;

    @org.junit.jupiter.api.BeforeEach
    void setup() {
        // 注册 ExceptionHandle 让 BaseException 正常转 R
        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setControllerAdvice(new com.aicode.config.ExceptionHandle())
                .build();
    }

    @Test
    @DisplayName("正常 init 流程：项目存在 + service.execute 不抛 → 返回 R.success()")
    void init_normal_returnsSuccess() throws Exception {
        doNothing().when(projectService).execute(any());

        mockMvc.perform(post("/project/init").param("code", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0000"));
    }

    @Test
    @DisplayName("空 code → Empty_Param（9004）")
    void init_emptyCode_throwsEmptyParam() throws Exception {
        // 空字符串经 Assert.hasText 直接抛 IllegalArgumentException
        // 由 @ControllerAdvice 转 R(9004)
        mockMvc.perform(post("/project/init").param("code", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("项目不存在 → service 抛 Result_Not_Exist → 400")
    void init_projectNotExists_throwsResultNotExist() throws Exception {
        doThrow(new ProjectException(com.aicode.core.BaseException.BaseExceptionEnum.Result_Not_Exist))
                .when(projectService).execute(any());

        mockMvc.perform(post("/project/init").param("code", "999"))
                .andExpect(status().isBadRequest());
    }
}
