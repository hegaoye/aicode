package com.aicode.project.ctrl;

import com.aicode.project.service.ProjectInitExecutor;
import com.aicode.project.service.ProjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 覆盖 `POST /project/init` 的 HTTP 层契约：
 * - 正常 init 流程（code 非空）→ 转交异步执行器并立即返回「已接受」
 * - 空 code → Empty_Param（9004）
 * - <b>项目不存在不再由 HTTP 层报错</b>（2026-10-18-init-async-progress 起）
 *
 * <p><b>⚠️ 2026-10-18 语义变更</b>：init 已改为「立即接受 + ws 推进度」。
 * 改造前"项目不存在"会由 {@code projectService.execute} 同步抛出
 * {@code Result_Not_Exist} → HTTP 400；改造后异常发生在响应之后，
 * 由 {@code ProjectInitExecutor} 捕获并经 ws 推送 —— <b>HTTP 恒返回 200 已接受</b>。</p>
 *
 * <p>这<b>不是</b>把错误藏起来了：失败原因仍会送达用户（ws 的 {@code error} 消息，
 * 含"已解析 i/N 张表"）。改造前那个 400 只有笼统的 "Server Error"，
 * 用户既不知道失败在哪一步，也不知道已完成多少。</p>
 *
 * <p>Controller 是薄包装：把 {@code ProjectInitExecutor} 打桩后，
 * 验证 Controller 只做"校验 + 转交"，不碰业务。</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class ProjectInitEndpointTest {

    @Autowired
    private ProjectController projectController;

    @MockitoBean private ProjectInitExecutor projectInitExecutor;
    @MockitoBean private ProjectService projectService;
    @MockitoBean private com.aicode.setting.service.SettingService settingService;
    @MockitoBean private com.aicode.project.service.ProjectFramworkService projectFramworkService;
    @MockitoBean private com.aicode.project.service.ProjectMapService projectMapService;
    @MockitoBean private com.aicode.project.service.ProjectModuleService projectModuleService;
    @MockitoBean private com.aicode.project.service.ProjectJobService projectJobService;
    @MockitoBean private com.aicode.frameworks.service.FrameworksService frameworksService;
    @MockitoBean private com.aicode.project.service.ProjectSqlService projectSqlService;
    @MockitoBean private com.aicode.project.service.ProjectRepositoryAccountService projectRepositoryAccountService;
    @MockitoBean private com.aicode.display.service.DisplayAttributeService displayAttributeService;
    @MockitoBean private com.aicode.map.service.MapRelationshipService mapRelationshipService;
    @MockitoBean private org.springframework.web.socket.server.standard.ServerEndpointExporter serverEndpointExporter;

    private MockMvc mockMvc;

    @org.junit.jupiter.api.BeforeEach
    void setup() {
        // 注册 ExceptionHandle 让 BaseException 正常转 R
        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setControllerAdvice(new com.aicode.config.ExceptionHandle())
                .build();
    }

    @Test
    @DisplayName("正常 init：把 code 转交异步执行器，HTTP 立即返回 R.success()")
    void init_normal_delegatesToAsyncExecutor() throws Exception {
        doNothing().when(projectInitExecutor).execute(anyString());

        mockMvc.perform(post("/project/init").param("code", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0000"));

        // Controller SHALL NOT 碰同步的 projectService —— 那是异步化前的路径
        verify(projectInitExecutor).execute("123");
        verify(projectService, never()).execute(anyString());
    }

    @Test
    @DisplayName("空 code → Empty_Param（9004），且不提交任何任务")
    void init_emptyCode_throwsEmptyParam() throws Exception {
        // 空字符串经 Assert.hasText 直接抛 IllegalArgumentException
        // 由 @ControllerAdvice 转 R(9004)
        mockMvc.perform(post("/project/init").param("code", ""))
                .andExpect(status().isBadRequest());

        verify(projectInitExecutor, never()).execute(anyString());
    }

    @Test
    @DisplayName("项目不存在：HTTP 仍返回 200 已接受（失败经 ws 送达，不在 HTTP 层报错）")
    void init_projectNotExists_returnsAccepted_notBadRequest() throws Exception {
        // 改造前：本用例断言 400（service 同步抛 Result_Not_Exist）。
        // 改造后：Controller 不再执行同步逻辑，异常发生在响应之后，
        // 故 HTTP SHALL 返回 200「已接受」，失败原因由 ws 的 error 消息送达。
        // 见类头 Javadoc 的「语义变更」段。
        mockMvc.perform(post("/project/init").param("code", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0000"));

        verify(projectInitExecutor).execute("999");
    }
}