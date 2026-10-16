package com.aicode.display;

import com.aicode.display.entity.DisplayAttribute;
import com.aicode.display.service.DisplayAttributeService;
import com.aicode.map.entity.MapClassTable;
import com.aicode.map.entity.MapFieldColumn;
import com.aicode.map.service.MapClassTableService;
import com.aicode.map.service.MapFieldColumnService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import cn.hutool.core.util.IdUtil;
import com.aicode.core.tools.JwtToken;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@code GET /displayAttribute/list} 的<b>响应形状</b>契约守护。
 *
 * <p><b>为什么这类测试不可省略</b>：把出参从实体换成 VO 属于「多写少写」型改动 ——
 * 多出来的字段<b>在运行期不会报错</b>，只会让 {@code contracts/openapi/openapi.yaml}
 * （M0 由 springdoc 导出）越来越臃肿，前端还会按契约生成类型并依赖上那些本不该存在的字段。
 * 上一轮 {@code MapFieldColumnMapperIT} 的事故已经证明：<b>没有断言的属性等于没有被测过</b>。
 * 因此本类以「SHALL NOT 出现」形式的负向断言为主。
 *
 * @see openspec/specs/api-data-object/spec.md
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class DisplayAttributeVoContractIT {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MapFieldColumnService mapFieldColumnService;
    @Autowired
    private MapClassTableService mapClassTableService;
    @Autowired
    private DisplayAttributeService displayAttributeService;

    /**
     * MOCK web 环境没有 {@code jakarta.websocket.server.ServerContainer}，
     * {@code WebSocketConfig} 注册的 {@code ServerEndpointExporter} 会因此创建失败。
     * 与 {@code TableMapperIT} / {@code MapFieldColumnMapperIT} 同样处理。
     */
    @MockitoBean
    private org.springframework.web.socket.server.standard.ServerEndpointExporter serverEndpointExporter;

    private MockMvc mockMvc;
    /**
     * 解析响应用的 mapper。本地构造而非注入 Spring bean：Jackson 3 下 Boot 4 暴露的是
     * {@code tools.jackson.databind.json.JsonMapper}，而本测试只关心「字段是否存在」
     * （Long→String 的序列化定制不影响该判定），故不引入 bean 依赖。
     */
    private static final JsonMapper PARSER = JsonMapper.builder().build();
    /** 本测试类独占的类模型编码，避免与其他集成测试的数据互相干扰。 */
    private String classTableCode;
    private String token;

    @BeforeEach
    void setUp() {
        // 手工构造：MockMvc 不会自动注册 Bean 形态的 Filter（见 AGENTS.md §10）
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        // 走真实鉴权链路：不 mock LoginInterceptor，而是签发一个合法 JWT
        token = JwtToken.createToken("admin", "display-attribute-vo-test");

        MapClassTable classTable = new MapClassTable();
        // code 非空且不由 MetaObjectHandler 自动填充 —— 生产由业务层用 Hutool 雪花赋值，此处对齐
        classTable.setCode(IdUtil.getSnowflakeNextIdStr());
        classTable.setTableName("vo_contract_tbl");
        classTable.setClassName("VoContractTbl");
        mapClassTableService.save(classTable);
        classTableCode = classTable.getCode();
    }

    // ================================================================
    // 负向断言：派生字段与嵌套对象 SHALL NOT 出现
    // ================================================================

    @Test
    @DisplayName("响应不含 5 个派生字段与嵌套的 mapRelationship（否则实体树会被固化进契约文件）")
    void response_omitsDerivedFieldsAndNestedEntities() throws Exception {
        seedDisplayAttribute(seedFieldColumn());

        JsonNode first = firstElement();

        assertThat(first.has("checkDate")).as("派生字段 checkDate 不应出现").isFalse();
        assertThat(first.has("checkState")).as("派生字段 checkState 不应出现").isFalse();
        assertThat(first.has("checkPk")).as("派生字段 checkPk 不应出现").isFalse();
        assertThat(first.has("checkDigit")).as("派生字段 checkDigit 不应出现").isFalse();
        assertThat(first.has("upper")).as("派生字段 upper 不应出现").isFalse();
        assertThat(first.has("mapRelationship"))
                .as("mapRelationship 内嵌 MapClassTable，后者持有 List<MapFieldColumn>，会形成不受控对象树")
                .isFalse();
    }

    @Test
    @DisplayName("响应元素只含 VO 声明的 13 个字段（不多不少）")
    void response_containsExactlyVoFields() throws Exception {
        seedDisplayAttribute(seedFieldColumn());

        // Jackson 3 的 JsonNode.propertyNames() 返回 Collection<String>（Jackson 2 只有 fieldNames()）
        assertThat(firstElement().propertyNames()).containsExactlyInAnyOrder(
                "id", "mapClassTableCode", "code", "column", "field",
                "sqlType", "fieldType", "notes", "defaultValue",
                "isPrimaryKey", "isDate", "isState", "displayAttribute");
    }

    // ================================================================
    // 正向断言：元驱动表单所需属性 SHALL 完整
    // ================================================================

    @Test
    @DisplayName("单次请求即含渲染表单所需的全部展示属性（无需回查第二个端点）")
    void response_carriesAllDisplayAttributesForFormRendering() throws Exception {
        DisplayAttribute attr = seedDisplayAttribute(seedFieldColumn());

        JsonNode da = firstElement().get("displayAttribute");

        assertThat(da).as("displayAttribute SHALL 出现").isNotNull();
        assertThat(da.get("displayType").asText())
                .as("前端据此选择控件").isEqualTo(attr.getDisplayType());
        assertThat(da.get("isRequired").asText()).isEqualTo(attr.getIsRequired());
        assertThat(da.get("matchType").asText())
                .as("前端据此决定查询算子").isEqualTo(attr.getMatchType());
        assertThat(da.get("validateText").asText()).isEqualTo(attr.getValidateText());
        assertThat(da.get("displayNo").asInt()).isEqualTo(attr.getDisplayNo());
        assertThat(da.has("isListPageDisplay")).as("列表可见性驱动列表列渲染").isTrue();
        assertThat(da.has("isDetailPageDisplay")).as("详情可见性驱动详情布局").isTrue();
        assertThat(da.has("fieldValidationMode")).as("校验方式驱动控件的校验行为").isTrue();
    }

    @Test
    @DisplayName("展示属性未配置时 displayAttribute 为 null，前端回退默认控件")
    void response_displayAttributeNull_whenNotConfigured() throws Exception {
        seedFieldColumn(); // 刻意不种 DisplayAttribute

        JsonNode da = firstElement().get("displayAttribute");

        assertThat(da.isNull())
                .as("未配置时 SHALL 为 null 而非缺键或空对象，前端据此判断是否走回退")
                .isTrue();
    }

    // ================================================================

    private JsonNode firstElement() throws Exception {
        MvcResult result = mockMvc.perform(get("/displayAttribute/list")
                        .param("mapClassTableCode", classTableCode)
                        .param("token", token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode data = PARSER.readTree(result.getResponse().getContentAsString()).get("data");
        assertThat(data).as("data SHALL 为数组").isNotNull();
        assertThat(data.isArray() && !data.isEmpty()).as("应至少有一条数据").isTrue();
        return data.get(0);
    }

    private MapFieldColumn seedFieldColumn() {
        MapFieldColumn column = new MapFieldColumn();
        column.setMapClassTableCode(classTableCode);
        column.setCode(IdUtil.getSnowflakeNextIdStr());
        column.setColumn("gift_name");
        column.setField("giftName");
        column.setSqlType("varchar");
        column.setFieldType("java.lang.String");
        column.setNotes("礼物名称");
        column.setDefaultValue("");
        column.setIsPrimaryKey("N");
        column.setIsDate("N");
        column.setIsState("N");
        mapFieldColumnService.save(column);
        return column;
    }

    private DisplayAttribute seedDisplayAttribute(MapFieldColumn column) {
        DisplayAttribute attr = new DisplayAttribute();
        attr.setMapClassTableCode(classTableCode);
        attr.setMapFieldColumnCode(column.getCode());
        attr.setDisplayType("Input");
        attr.setIsRequired("Y");
        attr.setMatchType("like");
        attr.setValidateText("请输入礼物名称");
        attr.setDisplayNo(1);
        attr.setIsListPageDisplay("Y");
        attr.setIsDetailPageDisplay("Y");
        attr.setFieldValidationMode("Required");
        displayAttributeService.save(attr);
        return attr;
    }
}