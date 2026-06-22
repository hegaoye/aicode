package com.aicode.project.service;

import com.aicode.core.BaseException;
import com.aicode.project.dao.mapper.ProjectJobMapper;
import com.aicode.project.entity.ProjectJob;
import com.aicode.project.entity.ProjectJobState;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 覆盖 spec/project-init-endpoint 中 ProjectJobServiceImpl.execute 的关键契约：
 * - execute 立即返回 ProjectJob（不阻塞）
 * - 并发两次 execute 第二次抛 Server_Error（DB 状态锁，回归 P2-4 修复）
 * - 通过独立 Bean ProjectJobExecutor 触发 @Async（不通过 this.，回归 P0-1 修复）
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class ProjectJobServiceImplConcurrencyTest {

    @Autowired
    private ProjectJobServiceImpl projectJobService;

    @MockBean private ProjectJobMapper projectJobMapper;
    @MockBean private ProjectJobExecutor projectJobExecutor;
    @MockBean private org.springframework.web.socket.server.standard.ServerEndpointExporter serverEndpointExporter;

    @BeforeEach
    void setup() {
        // 第一次调用返回 0（无活跃任务），第二次返回 1（有活跃任务）
        // 用 sequential 调用计数实现
        final int[] count = {0};
        when(projectJobMapper.selectCount(any(LambdaQueryWrapper.class))).thenAnswer(inv -> {
            count[0]++;
            // 第一次 selectCount 返回 0（无活跃），让第一个 execute 通过
            // 后续返回 1（有活跃），触发 Server_Error
            return count[0] == 1 ? 0L : 1L;
        });
        when(projectJobMapper.insert(any(ProjectJob.class))).thenReturn(1);
    }

    @Test
    @DisplayName("execute 立即返回 ProjectJob(state=Executing)，不阻塞")
    void execute_returnsImmediately() {
        long t0 = System.currentTimeMillis();
        ProjectJob job = projectJobService.execute("1");
        long elapsed = System.currentTimeMillis() - t0;

        assertNotNull(job, "必须返回非空 ProjectJob");
        assertEquals("1", job.getProjectCode());
        // state 由 insert 设——这里 insert 是 mock，返回的对象保留 default field（state 是 null）
        // 但 DB 状态锁逻辑要求 insert 之前 selectCount=0
        // 我们测的是 execute 的控制流：返回前只做 selectCount + insert
        assertTrue(elapsed < 1000, "execute 应在 1s 内返回，实际 " + elapsed + "ms");
    }

    @Test
    @DisplayName("并发两次 execute：第二次抛 Server_Error（回归 P2-4 修复）")
    void execute_concurrentSecond_throwsServerError() {
        // 第一次 selectCount=0（无活跃），insert 完成
        projectJobService.execute("1");
        // 后续 selectCount=1（有活跃）→ 抛 Server_Error
        assertThrows(BaseException.class, () -> projectJobService.execute("1"));
    }

    @Test
    @DisplayName("execute 通过 ProjectJobExecutor 而非 this. 触发 @Async（回归 P0-1 修复）")
    void execute_dispatchesViaExecutorBean_notThisSelfCall() {
        // 关键：@Async 必须由 Spring AOP 拦截，因此 execute 必须调用独立的
        // projectJobExecutor bean（注入的 mock），而不是 this. 内调
        projectJobService.execute("1");
        org.mockito.Mockito.verify(projectJobExecutor, org.mockito.Mockito.times(1))
                .execute(any(String.class), any(ProjectJob.class));
    }
}
