package com.aicode.map.ctrl;

import com.aicode.core.BaseException;
import com.aicode.core.enums.YNEnum;
import com.aicode.map.entity.MapRelationship;
import com.aicode.map.service.MapClassTableService;
import com.aicode.map.service.MapFieldColumnService;
import com.aicode.map.service.MapRelationshipService;
import com.aicode.project.service.ProjectMapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 覆盖 spec/map-relationship-crud 的关键契约。
 *
 * 策略：直接注入 controller bean，绕过 MockMvc 的 enum String→YNEnum 绑定
 * 复杂性（避免引入自定义 WebMvcConfigurer）。Controller 内部逻辑完整，测试
 * 覆盖正反向关联方向保持。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class MapRelationshipControllerTest {

    @Autowired
    private MapRelationshipController controller;

    @MockBean private MapRelationshipService mapRelationshipService;
    @MockBean private ProjectMapService projectMapService;
    @MockBean private MapClassTableService mapClassTableService;
    @MockBean private MapFieldColumnService mapFieldColumnService;
    @MockBean private com.baidu.fsg.uid.UidGenerator uidGenerator;
    @MockBean private org.springframework.web.socket.server.standard.ServerEndpointExporter serverEndpointExporter;

    @BeforeEach
    void setup() {
        when(uidGenerator.getUID()).thenReturn(100L);
        when(mapRelationshipService.getOne(any())).thenReturn(null);
        when(mapRelationshipService.saveOrUpdate(any(MapRelationship.class))).thenReturn(true);
    }

    @Test
    @DisplayName("新建一对多关联：保存两次（正向 + 反向）")
    void build_oneToMany_persistsBothDirections() {
        controller.build("CT_A", "CT_B", YNEnum.N, YNEnum.Y, "userId", "orderUserId");
        verify(mapRelationshipService, times(2)).saveOrUpdate(any(MapRelationship.class));
    }

    @Test
    @DisplayName("反向方向：正向 OneToMany → 反向 OneToOne（回归 P1-3 修复）")
    void build_oneToManyReverseIsOneToOne() {
        org.mockito.ArgumentCaptor<MapRelationship> captor =
                org.mockito.ArgumentCaptor.forClass(MapRelationship.class);

        controller.build("CT_A", "CT_B", YNEnum.N, YNEnum.Y, "userId", "orderUserId");

        verify(mapRelationshipService, times(2)).saveOrUpdate(captor.capture());
        java.util.List<MapRelationship> saved = captor.getAllValues();
        // 反向关联：isOneToOne=Y, isOneToMany=N
        MapRelationship reverse = saved.get(1);
        assertEquals("Y", reverse.getIsOneToOne());
        assertEquals("N", reverse.getIsOneToMany());
    }

    @Test
    @DisplayName("反向方向：正向 OneToOne → 反向 OneToOne（保持方向）")
    void build_oneToOneReverseIsOneToOne() {
        org.mockito.ArgumentCaptor<MapRelationship> captor =
                org.mockito.ArgumentCaptor.forClass(MapRelationship.class);

        controller.build("CT_A", "CT_B", YNEnum.Y, YNEnum.N, "userId", "profileUserId");

        verify(mapRelationshipService, times(2)).saveOrUpdate(captor.capture());
        MapRelationship reverse = captor.getAllValues().get(1);
        assertEquals("Y", reverse.getIsOneToOne());
        assertEquals("N", reverse.getIsOneToMany());
    }

    @Test
    @DisplayName("反向 mainField/joinField 互换")
    void build_mainFieldJoinFieldSwapped() {
        org.mockito.ArgumentCaptor<MapRelationship> captor =
                org.mockito.ArgumentCaptor.forClass(MapRelationship.class);

        controller.build("CT_A", "CT_B", YNEnum.N, YNEnum.Y, "userId", "orderUserId");

        verify(mapRelationshipService, times(2)).saveOrUpdate(captor.capture());
        MapRelationship reverse = captor.getAllValues().get(1);
        assertEquals("orderUserId", reverse.getMainField());
        assertEquals("userId", reverse.getJoinField());
    }

    @Test
    @DisplayName("缺 mainField 抛 Empty_Param 异常（不调 saveOrUpdate）")
    void build_missingMainField_throws() {
        assertThrows(Exception.class, () ->
                controller.build("CT_A", "CT_B", YNEnum.N, YNEnum.Y, "", "orderUserId"));
        verify(mapRelationshipService, times(0)).saveOrUpdate(any(MapRelationship.class));
    }
}
