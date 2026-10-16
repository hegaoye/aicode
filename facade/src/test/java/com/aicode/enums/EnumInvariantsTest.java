package com.aicode.enums;

import com.aicode.display.entity.DisplayAttributeDisplayType;
import com.aicode.display.entity.DisplayAttributeFieldValidationMode;
import com.aicode.project.entity.ProjectFramworkState;
import com.aicode.project.entity.ProjectJobLogsState;
import com.aicode.project.entity.ProjectJobState;
import com.aicode.project.entity.ProjectMapState;
import com.aicode.project.entity.ProjectModuleState;
import com.aicode.project.entity.ProjectRepositoryAccountState;
import com.aicode.project.entity.ProjectRepositoryTypeEnum;
import com.aicode.project.entity.ProjectSqlState;
import com.aicode.project.entity.ProjectState;
import com.aicode.setting.entity.SettingState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * `facade` 模块枚举的通用不变量测试。
 *
 * <p>`facade` 自建库以来<b>零测试</b>（AGENTS.md §7.10 第 2 项列的空白点）。本测试
 * 用参数化把 12 个枚举的<b>共同不变量</b>一次钉死，避免为每个枚举写一份几乎相同的样板：</p>
 *
 * <ol>
 *   <li>{@code getEnum(name)} 对 {@code name()} 大小写不敏感；</li>
 *   <li>{@code getEnum(未知值)} / {@code getEnum("")} / {@code getEnum(null)} 返 {@code null}
 *       （不抛异常）；</li>
 *   <li>每个枚举成员的中文标签字段 {@code val} 非空 —— 该字段最终出现在模板与
 *       {@code @Schema(description)} 里，为空会让契约层描述退化。</li>
 * </ol>
 *
 * <p>对语义关键的枚举（{@link ProjectJobState} / {@link ProjectState} /
 * {@link DisplayAttributeDisplayType}）另加专用断言，钉住被前端消费的契约面。</p>
 *
 * @see <a href="file:../../../../../openspec/specs/api-contract/spec.md">api-contract 能力</a>
 */
@DisplayName("facade 枚举通用不变量")
class EnumInvariantsTest {

    /** 一个枚举的最小适配视图：把成员名、中文标签、getEnum 入口打包，供参数化复用。 */
    private record EnumCase(String label,
                            List<String> names,
                            List<String> values,
                            Function<String, Object> getEnum) {
    }

    static Stream<EnumCase> enumCases() {
        return Stream.of(
                caseOf("ProjectState", ProjectState.values(), ProjectState::getEnum,
                        e -> e.val),
                caseOf("ProjectSqlState", ProjectSqlState.values(), ProjectSqlState::getEnum,
                        e -> e.val),
                caseOf("ProjectFramworkState", ProjectFramworkState.values(), ProjectFramworkState::getEnum,
                        e -> e.val),
                caseOf("ProjectJobState", ProjectJobState.values(), ProjectJobState::getEnum,
                        e -> e.val),
                caseOf("ProjectJobLogsState", ProjectJobLogsState.values(), ProjectJobLogsState::getEnum,
                        e -> e.val),
                caseOf("ProjectMapState", ProjectMapState.values(), ProjectMapState::getEnum,
                        e -> e.val),
                caseOf("ProjectModuleState", ProjectModuleState.values(), ProjectModuleState::getEnum,
                        e -> e.val),
                caseOf("ProjectRepositoryAccountState", ProjectRepositoryAccountState.values(),
                        ProjectRepositoryAccountState::getEnum, e -> e.val),
                caseOf("ProjectRepositoryTypeEnum", ProjectRepositoryTypeEnum.values(),
                        ProjectRepositoryTypeEnum::getEnum, e -> e.val),
                caseOf("SettingState", SettingState.values(), SettingState::getEnum, e -> e.val),
                caseOf("DisplayAttributeDisplayType", DisplayAttributeDisplayType.values(),
                        DisplayAttributeDisplayType::getEnum, e -> e.val),
                caseOf("DisplayAttributeFieldValidationMode", DisplayAttributeFieldValidationMode.values(),
                        DisplayAttributeFieldValidationMode::getEnum, e -> e.val)
        );
    }

    private static <E extends Enum<E>> EnumCase caseOf(String label,
                                                      E[] values,
                                                      Function<String, E> getEnum,
                                                      Function<E, String> valGetter) {
        return new EnumCase(
                label,
                Arrays.stream(values).map(Enum::name).toList(),
                Arrays.stream(values).map(valGetter).toList(),
                getEnum::apply);
    }

    @ParameterizedTest(name = "{0}：getEnum 大小写不敏感")
    @MethodSource("enumCases")
    @DisplayName("getEnum 对每个成员名及其小写变体都能命中")
    void getEnum_isCaseInsensitive(EnumCase c) {
        for (String name : c.names()) {
            assertThat(c.getEnum().apply(name))
                    .as("%s.getEnum(\"%s\") 应命中", c.label(), name)
                    .isNotNull()
                    .extracting(Object::toString)
                    .isEqualTo(name);

            assertThat(c.getEnum().apply(name.toLowerCase()))
                    .as("%s.getEnum(\"%s\")（小写）应命中同一成员", c.label(), name.toLowerCase())
                    .isNotNull()
                    .extracting(Object::toString)
                    .isEqualTo(name);
        }
    }

    @ParameterizedTest(name = "{0}：未知值返 null")
    @MethodSource("enumCases")
    @DisplayName("getEnum 对未知值、空串与 null 均返 null（不抛异常）")
    void getEnum_unknownOrNull_returnsNull(EnumCase c) {
        assertThat(c.getEnum().apply("__no_such_enum_member__"))
                .as("%s.getEnum(未知值) 应返 null", c.label())
                .isNull();
        assertThat(c.getEnum().apply(""))
                .as("%s.getEnum(空串) 应返 null", c.label())
                .isNull();
        assertThat(c.getEnum().apply(null))
                .as("%s.getEnum(null) 应返 null 而非抛 NPE", c.label())
                .isNull();
    }

    @ParameterizedTest(name = "{0}：val 标签非空")
    @MethodSource("enumCases")
    @DisplayName("每个枚举成员的中文标签 val 非空（契约 description 依赖它）")
    void everyMember_hasNonBlankVal(EnumCase c) {
        assertThat(c.values()).hasSameSizeAs(c.names());
        for (int i = 0; i < c.values().size(); i++) {
            assertThat(c.values().get(i))
                    .as("%s.%s 的 val 标签 SHALL 非空", c.label(), c.names().get(i))
                    .isNotBlank();
        }
    }

    @Test
    @DisplayName("ProjectJobState 语义锚定：构建状态机 5 态且名字被前端 M3-B 日志面板引用")
    void projectJobState_hasFiveStatesWithStableNames() {
        assertThat(ProjectJobState.values())
                .extracting(Enum::name)
                .containsExactly("Create", "Executing", "Completed", "Error", "Waring");
    }

    @Test
    @DisplayName("ProjectState 语义锚定：项目 3 态且 Enable/Disenable 被前端构建按钮的 disabled 判定消费")
    void projectState_hasThreeStatesWithStableNames() {
        assertThat(ProjectState.values())
                .extracting(Enum::name)
                .containsExactly("Enable", "Disenable", "Delete");
        // 前端 ProjectListPage 用 `row.state === 'Enable'` 决定构建按钮是否可用
        assertThat(ProjectState.getEnum("Enable")).isEqualTo(ProjectState.Enable);
        assertThat(ProjectState.getEnum("Disenable")).isEqualTo(ProjectState.Disenable);
    }

    @Test
    @DisplayName("DisplayAttributeDisplayType 语义锚定：前端 DisplayControl 分发的 4 类字面仍在枚举内")
    void displayAttributeDisplayType_containsFrontendDispatchedLiterals() {
        // M1 的 `src/lib/display-controls/index.tsx` 的 CONTROLS 表按 displayType 字面分发。
        // 这 4 个字面被契约导出为可选项；若枚举删除它们，前端分发会静默退化为 unknown 兜底。
        assertThat(DisplayAttributeDisplayType.getEnum("Input")).isNotNull();
        assertThat(DisplayAttributeDisplayType.getEnum("InputNumber")).isNotNull();
        assertThat(DisplayAttributeDisplayType.getEnum("DatePicker")).isNotNull();
        assertThat(DisplayAttributeDisplayType.getEnum("Select")).isNotNull();
        assertThat(DisplayAttributeDisplayType.values().length)
                .as("控件类型总数（AGENTS §5.4 记录为 23 种）SHALL 不少于 20")
                .isGreaterThanOrEqualTo(20);
    }
}