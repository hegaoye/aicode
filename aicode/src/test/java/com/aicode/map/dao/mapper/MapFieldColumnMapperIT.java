package com.aicode.map.dao.mapper;

import com.aicode.map.entity.MapFieldColumn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@code MapFieldColumn} 的<b>真实 SQL</b>集成回归 —— 守护「Java 字段 ↔ 数据库列」的映射。
 *
 * <p><b>为什么必须有这个测试类</b>：2026-10-15 发生过一次真实事故 ——
 * 变更 {@code 2026-10-15-dead-code-and-dependency-hygiene} 的任务组 4 把
 * {@code mapClassTableCode} 等字段从 {@code MapFieldColumn} 移入新抽取的基类 {@code AbstractField}，
 * 而 Java 的字段注解只能声明在定义该字段的类上，导致 {@code MapFieldColumn} 上
 * <b>12 个 {@code @TableField} 全部丢失</b>（7 个显式列名 + 5 个 {@code exist = false}）。
 * MyBatis-Plus 随即回退到默认命名策略，把 camelCase 字段转成 snake_case 列：
 *
 * <pre>
 * Column "map_class_table_code" not found; SQL statement:
 * SELECT id,map_class_table_code,code,column,field,sql_type,field_type,notes,
 *        default_value,is_primary_key,is_date,is_state,check_date,check_state,
 *        check_pk,check_digit,upper FROM map_field_column WHERE (map_class_table_code = ?)
 * </pre>
 *
 * 代码生成主链路因此完全不可用，而<b>当时全部 215 个测试都是绿的</b>。
 * 原因：既有测试没有任何一个让 MyBatis-Plus 为该实体生成 SQL 并打到数据库上 ——
 * 业务层用 {@code new MapFieldColumn()} + setter 纯内存装配，生成链路的测试把 mapper 打了 mock。
 *
 * <p><b>因此本类的三条断言各自守护不同的失败模式</b>：
 * <ol>
 *   <li>{@link #selectList_byExplicitColumnName_findsRow()} —— 守护「显式列名映射生效」（列名写错）；</li>
 *   <li>{@link #insertAndReload_allPersistedColumns_roundTrip()} —— 守护「12 个真实列双向可读可写」（漏映射 / 映射反了）；</li>
 *   <li>{@link #excludedFields_areAbsentFromGeneratedSelect()} —— 守护「{@code exist = false} 生效」，
 *       即那些<b>本不该出现</b>的派生列不得混入 SQL。若表的列恰好存在，此类错误不会报错，
 *       只会在写入时抛异常 —— 仅断言「能查通」不足以覆盖它。</li>
 * </ol>
 *
 * <p><b>扩展注意</b>：本类依赖 {@code db/schema.sql} 中的真实列名。若未来调整 schema，
 * 测试失败是<b>期望行为</b>（提示同步更新实体的 {@code @TableField}），不是测试自身的问题。
 *
 * @see openspec/specs/dependency-hygiene/spec.md「实体与数据库列的映射 SHALL 由真实 SQL 测试守护」
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class MapFieldColumnMapperIT {

    @Autowired
    private MapFieldColumnMapper mapFieldColumnMapper;

    /**
     * MOCK web 环境没有 {@code jakarta.websocket.server.ServerContainer}，
     * {@code WebSocketConfig} 注册的 {@code ServerEndpointExporter} 会因此创建失败。
     * 与 {@code TableMapperIT} 同样处理。
     */
    @MockitoBean
    private org.springframework.web.socket.server.standard.ServerEndpointExporter serverEndpointExporter;

    @BeforeEach
    void cleanUp() {
        mapFieldColumnMapper.delete(null);
    }

    // ================================================================
    // 失败模式 1：显式列名映射失效（本次事故的直接表现）
    // ================================================================

    @Test
    @DisplayName("按 mapClassTableCode 条件查询能命中 —— 证明 @TableField(\"mapClassTableCode\") 生效")
    void selectList_byExplicitColumnName_findsRow() {
        // 这里刻意只依赖 MapFieldColumn —— 本测试守护的是它一个实体的列映射，
        // 引入其他实体会把它的失败与其他实体的映射问题混在一起。
        final String mappingCode = "mapctc-it-0001";
        MapFieldColumn column = newField(mappingCode, "gift_name", "varchar", "giftName");
        mapFieldColumnMapper.insert(column);

        List<MapFieldColumn> found = mapFieldColumnMapper
                .selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MapFieldColumn>()
                        .eq(MapFieldColumn::getMapClassTableCode, mappingCode));

        assertThat(found)
                .as("SQL 会按 map_class_table_code 过滤；若 @TableField 丢失则该列不存在，查询直接报错")
                .hasSize(1);
        assertThat(found.get(0).getColumn()).isEqualTo("gift_name");
    }

    // ================================================================
    // 失败模式 2：漏映射 / 映射方向反了（能查通但值不对）
    // ================================================================

    @Test
    @DisplayName("插入后 12 个真实列逐个读回一致 —— 证明每个 camelCase 列都映射正确")
    void insertAndReload_allPersistedColumns_roundTrip() {
        MapFieldColumn column = newField("gift_record", "gift_code", "varchar", "giftCode");
        column.setFieldType("java.lang.String");
        column.setNotes("礼物编码");
        column.setDefaultValue("'N/A'");
        column.setIsPrimaryKey("N");
        column.setIsDate("N");
        column.setIsState("Y");
        mapFieldColumnMapper.insert(column);

        MapFieldColumn reloaded = mapFieldColumnMapper.selectById(column.getId());

        assertThat(reloaded).isNotNull();
        assertThat(reloaded.getId()).isEqualTo(column.getId());
        assertThat(reloaded.getMapClassTableCode()).isEqualTo("gift_record");
        assertThat(reloaded.getCode()).isEqualTo(column.getCode());
        assertThat(reloaded.getColumn()).isEqualTo("gift_code");
        assertThat(reloaded.getField()).isEqualTo("giftCode");
        assertThat(reloaded.getSqlType()).isEqualTo("varchar");
        assertThat(reloaded.getFieldType()).isEqualTo("java.lang.String");
        assertThat(reloaded.getNotes()).isEqualTo("礼物编码");
        assertThat(reloaded.getDefaultValue()).isEqualTo("'N/A'");
        assertThat(reloaded.getIsPrimaryKey()).isEqualTo("N");
        assertThat(reloaded.getIsDate()).isEqualTo("N");
        assertThat(reloaded.getIsState()).isEqualTo("Y");
    }

    // ================================================================
    // 失败模式 3：exist = false 失效（派生列混入 SQL）
    // ================================================================

    @Test
    @DisplayName("@TableField(exist=false) 的 5 个派生字段不出现在生成的 SELECT 中")
    void excludedFields_areAbsentFromGeneratedSelect() {
        MapFieldColumn column = newField("gift_record", "gift_name", "varchar", "giftName");
        mapFieldColumnMapper.insert(column);

        // MyBatis-Plus 会为整个实体生成 SELECT；若 exist=false 失效，
        // check_date / check_state / check_pk / check_digit / upper 会一并被查出来。
        // 这些列在 schema 中并不存在，因此查询会直接失败 —— 这正是本次事故的伴随症状。
        List<MapFieldColumn> all = mapFieldColumnMapper.selectList(null);

        assertThat(all).hasSize(1);
        assertThat(all.get(0).getClass().getDeclaredFields())
                .as("5 个派生字段应带 @TableField(exist = false)")
                .filteredOn(f -> f.getName().equals("checkDate")
                        || f.getName().equals("checkState")
                        || f.getName().equals("checkPk")
                        || f.getName().equals("checkDigit")
                        || f.getName().equals("upper"))
                .hasSize(5);
    }

    // ================================================================

    private MapFieldColumn newField(String mapClassTableCode, String columnName,
                                    String sqlType, String field) {
        MapFieldColumn f = new MapFieldColumn();
        f.setMapClassTableCode(mapClassTableCode);
        f.setCode("code-" + columnName);
        f.setColumn(columnName);
        f.setSqlType(sqlType);
        f.setNotes("备注-" + columnName);
        f.setDefaultValue("");
        f.setIsPrimaryKey("N");
        f.setIsDate("N");
        f.setIsState("N");
        f.setField(field);
        return f;
    }
}
