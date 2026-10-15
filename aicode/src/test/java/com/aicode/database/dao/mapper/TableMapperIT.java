package com.aicode.database.dao.mapper;

import com.aicode.database.entity.Column;
import com.aicode.database.entity.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 元数据探测的集成回归测试 —— 守护 {@code INFORMATION_SCHEMA} 的查询语义。
 *
 * <p><b>与 {@code ApiContractIT} 的分工（两者不可相互替代）</b>：</p>
 * <pre>
 * ApiContractIT    守护 REST 层：路径后缀、响应结构、ID 字符串化、日期格式
 * TableMapperIT    守护元数据层：INFORMATION_SCHEMA 查询语义   ← 本类
 * </pre>
 *
 * <p><b>为什么必须用真实数据库</b>：本类要守护的正是「数据库元数据长什么样」。
 * 若 mock 掉 mapper，等于把要守护的东西 mock 掉了。
 * 2026-10-15 的事故正是如此 —— H2 从 1.4.200 升到 2.4.240 后
 * {@code TABLE_TYPE} 由 {@code 'TABLE'} 变为 SQL 标准值 {@code 'BASE TABLE'}，
 * {@code table.xml} 中硬编码的旧值使三处查询恒定匹配 0 行，
 * 而当时没有任何测试能在 CI 阶段发现它。</p>
 *
 * <p><b>扩展本测试时请一并复核</b> {@code mapper/database/table.xml} 与
 * {@code colum.xml} 中的字面量：凡是与数据库版本相关的取值，都应在此有对应断言。</p>
 *
 * @see <a href="file:../../../../../../../../../../main/resources/mapper/database/table.xml">table.xml</a>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class TableMapperIT {

    /** 测试专属 schema 前缀，避免与 application-test.yml 初始化的业务表冲突 */
    private static final String SCHEMA = "it_metadisc_tbl";
    private static final String TABLE = "gift_record";

    @Autowired
    private TableMapper tableMapper;

    @Autowired
    private ColumMapper columMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * MOCK 环境（{@code WebEnvironment.MOCK}）不启动嵌入式容器，不存在
     * {@code jakarta.websocket.server.ServerContainer}，会导致
     * {@code WebSocketConfig#serverEndpointExporter} 创建失败、应用上下文直接起不来。
     * 与其他 {@code @SpringBootTest(MOCK)} 测试类同样的处理方式。
     */
    @MockitoBean
    private org.springframework.web.socket.server.standard.ServerEndpointExporter serverEndpointExporter;

    @BeforeEach
    void prepare() {
        // 每次从干净状态开始：先清后建，确保断言不受上次执行残留影响
        jdbcTemplate.execute("DROP SCHEMA IF EXISTS " + SCHEMA + " CASCADE");
        jdbcTemplate.execute("CREATE SCHEMA " + SCHEMA);
        jdbcTemplate.execute("USE " + SCHEMA);
        jdbcTemplate.execute("""
                CREATE TABLE gift_record (
                    id          bigint      NOT NULL,
                    game_room_id bigint     NULL,
                    gift_name   varchar(64) NULL,
                    price       double      NULL,
                    PRIMARY KEY (id)
                )""");
        // 附带建一个视图：验证探测 SHALL NOT 把视图当作表返回
        jdbcTemplate.execute("CREATE VIEW gift_view AS SELECT id FROM gift_record");
        jdbcTemplate.execute("USE PUBLIC");
    }

    // ---------- 场景①②③：list / load / count ----------

    @Test
    @DisplayName("list：能返回刚创建的表（TABLE_TYPE 与 H2 当前版本一致）")
    void list_returnsCreatedTable() {
        List<Table> tables = tableMapper.list(SCHEMA);

        assertThat(tables)
                .as("元数据探测 SHALL 能识别 H2 2.x 返回的 'BASE TABLE' 类型值")
                .isNotEmpty();
        assertThat(tables).extracting(Table::getTableName).contains(TABLE);
    }

    @Test
    @DisplayName("list：SHALL NOT 把视图当作表返回")
    void list_excludesViews() {
        List<Table> tables = tableMapper.list(SCHEMA);

        assertThat(tables).extracting(Table::getTableName).doesNotContain("gift_view");
    }

    @Test
    @DisplayName("load：能按表名取到该表信息")
    void load_returnsSpecificTable() {
        Table table = tableMapper.load(SCHEMA, TABLE);

        assertThat(table).isNotNull();
        assertThat(table.getTableName()).isEqualTo(TABLE);
    }

    @Test
    @DisplayName("load：表不存在时返回 null")
    void load_returnsNullWhenTableAbsent() {
        // TableMapper#load 返回单个对象，MyBatis 在无匹配行时返回 null
        // （EmptyResultDataAccessException 出现在 selectOne 的调用侧，此处未被触发）
        assertThat(tableMapper.load(SCHEMA, "no_such_table")).isNull();
    }

    @Test
    @DisplayName("count：返回该 schema 下的表数量（不含视图）")
    void count_returnsTableCount() {
        assertThat(tableMapper.count(SCHEMA))
                .as("count 与 list SHALL 口径一致：都只数基础表")
                .isEqualTo(tableMapper.list(SCHEMA).size())
                .isGreaterThanOrEqualTo(1);
    }

    // ---------- 场景④：列探测 ----------

    @Test
    @DisplayName("列探测：返回该表的全部列定义")
    void columnList_returnsAllColumns() {
        List<Column> columns = columMapper.list(SCHEMA, TABLE);

        assertThat(columns)
                .as("列探测 SHALL 走 INFORMATION_SCHEMA.COLUMNS")
                .isNotEmpty();
        assertThat(columns).extracting(Column::getColumnName)
                .contains("id", "game_room_id", "gift_name", "price");
    }

    @Test
    @DisplayName("列探测：typeName SHALL 被正确填充（DATA_TYPE 的别名映射）")
    void columnList_mapsTypeName() {
        // 守卫 colum.xml 的第二层陷阱：H2 2.x 的列名是 DATA_TYPE，
        // 而 Column 实体的属性名是 typeName（由 @TableField 指定）。
        // 两者不是驼峰对应关系，不加 AS 别名时 typeName 会静默为 null，
        // 进而在 DatabaseDataTypesUtils.isDate() 抛 NPE ——
        // 把一个 SQL 层的错误伪装成空指针。
        List<Column> columns = columMapper.list(SCHEMA, TABLE);

        assertThat(columns).isNotEmpty();
        assertThat(columns)
                .as("每列的 typeName SHALL 非空 —— 为空说明 DATA_TYPE 的别名映射失效")
                .allSatisfy(c -> assertThat(c.getTypeName())
                        .as("列 %s 的 typeName", c.getColumnName())
                        .isNotBlank());
        assertThat(columns).filteredOn(c -> "gift_name".equals(c.getColumnName()))
                .first()
                .extracting(Column::getTypeName)
                .asString()
                .containsIgnoringCase("CHARACTER");
    }

    // ---------- 场景⑤：空 schema 边界 ----------

    @Test
    @DisplayName("空 schema：list 返回空结果，count 返回 0")
    void emptySchema_returnsEmptyAndZero() {
        jdbcTemplate.execute("CREATE SCHEMA it_metadisc_empty");

        assertThat(tableMapper.list("it_metadisc_empty"))
                .as("空 schema 的 list SHALL 返回空列表 —— 该边界须可被正确表达")
                .isEmpty();
        assertThat(tableMapper.count("it_metadisc_empty")).isZero();
    }

    @Test
    @DisplayName("不存在的 schema：list 返回空而非抛异常")
    void missingSchema_returnsEmpty() {
        assertThat(tableMapper.list("it_no_such_schema")).isEmpty();
        assertThat(tableMapper.count("it_no_such_schema")).isZero();
    }

    // ---------- 版本绑定守卫 ----------

    @Test
    @DisplayName("守卫：当前 H2 版本的基础表类型值 SHALL 为 'BASE TABLE'")
    void currentH2TableTypeIsBaseTable() {
        // 本断言是本次事故的哨兵：若将来 H2 再次改变该取值，本测试会先于
        // 使用者手工点「解析」失败，从而逼迫复核 table.xml 中的硬编码字面量。
        String tableType = jdbcTemplate.queryForObject(
                "SELECT TABLE_TYPE FROM INFORMATION_SCHEMA.TABLES "
                        + "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?",
                String.class, SCHEMA, TABLE);

        assertThat(tableType)
                .as("H2 的 TABLE_TYPE 取值随版本变化。若此断言失败，SHALL 一并复核 "
                        + "mapper/database/table.xml 中的 TABLE_TYPE 字面量")
                .isEqualTo("BASE TABLE");
    }
}
