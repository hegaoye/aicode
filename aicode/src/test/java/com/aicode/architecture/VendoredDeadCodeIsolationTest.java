package com.aicode.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import java.util.Set;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * 防回流断言：禁止重新引入对 {@code common} 中 vendored 死代码区域的引用。
 *
 * <p>背景：{@code com.aicode.core.tools.core} 下约 8000 行代码是从 2010 年前后的
 * 「仁中和 AI-Code」、Apache MyBatis Generator、Velocity 及 Spring 3.0 源码逐字 vendored 进来的
 * （含 {@code paranamer} / {@code model.java} / {@code sqlparse} 子树、{@code AntPathMatcher}、
 * {@code XMLHelper} 等），经调用图验证无任何生产调用方。本断言在删除动作<b>之前</b>落地并通过，
 * 其通过结果即构成「该区域无编译期引用」的机器证据；删除后继续充当防回流围栏。
 *
 * <p><b>能力边界（重要）</b>：本断言仅覆盖<b>编译期类型引用</b>。
 * 反射调用、注解引用（如 {@code @CacheEnum} / {@code @PK} / {@code @IdempotentLock} / {@code @Ignore}）、
 * MyBatis XML 与资源文件中的引用均<b>不</b>在其覆盖范围内，删除前须由人工核查补充。
 * 详见 {@code openspec/changes/dead-code-and-dependency-hygiene/design.md - D1}。
 */
@AnalyzeClasses(packages = "com.aicode", importOptions = ImportOption.DoNotIncludeTests.class)
class VendoredDeadCodeIsolationTest {

    /** vendored 死代码区域的根包。 */
    private static final String DEAD_REGION = "com.aicode.core.tools.core";

    /**
     * 该区域内仍被生产代码引用的类，豁免于本断言。改动此集合前须同步复核调用方。
     *
     * <p>说明：本集合曾包含 {@code AbstractField} —— 那是 2026-10-15 为消除
     * {@code Field} / {@code MapFieldColumn} 字段重复而抽取的基类。该抽取已回退：
     * 字段注解只能声明在定义该字段的类上，把 {@code mapClassTableCode} 等字段移入基类
     * 会连带丢失 12 个 {@code @TableField}，导致代码生成主链路完全不可用。
     * 详见 {@code openspec/changes/fix-map-field-column-tablefield-regression/}。
     */
    private static final Set<String> LIVE_CLASSES = Set.of(
            DEAD_REGION + ".StringHelper",
            DEAD_REGION + ".Field",
            DEAD_REGION + ".typemapping.DatabaseDataTypesUtils");

    private static final DescribedPredicate<JavaClass> IS_LIVE =
            new DescribedPredicate<JavaClass>("属于豁免的活类 %s（含其编译生成的嵌套类）", LIVE_CLASSES) {
                @Override
                public boolean test(JavaClass input) {
                    String name = input.getName();
                    for (String live : LIVE_CLASSES) {
                        // 同时豁免 Lombok 等注解处理器生成的嵌套类（如 Xxx$XxxBuilder），
                        // 否则每引入一个 builder 都要手工登记一次，豁免集会迅速失效。
                        if (name.equals(live) || name.startsWith(live + "$")) {
                            return true;
                        }
                    }
                    return false;
                }
            };

    @ArchTest
    static final ArchRule 下游模块不得引用vendored死代码区域 = noClasses()
            .that().resideOutsideOfPackage(DEAD_REGION + "..")
            .should().dependOnClassesThat(
                    resideInAPackage(DEAD_REGION + "..").and(not(IS_LIVE)))
            .because("common 中的 vendored 死代码区域已确认无调用方并被物理删除，"
                    + "重新引入只会让制品体积与维护成本回升；"
                    + "如确需新能力，应在有明确调用方的新类中实现");
}
