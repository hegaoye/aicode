package com.aicode.config.template;

import com.alibaba.fastjson2.JSON;
import cn.hutool.core.bean.BeanUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 守护「{@code TemplateData} 展开为模板变量」的实现选择。
 *
 * <p><b>背景</b>：两个 {@code TemplateHelper} 实现（{@code FreemarkerHelper} / {@code BeetlHelper}）
 * 都用 {@code JSON.parseObject(JSON.toJSONString(templateData), Map.class)} 做 bean → String → Map 的往返。
 * 这个往返<b>看起来是浪费</b>（多一次完整序列化），曾计划替换为 {@code hutool BeanUtil.beanToMap}。
 *
 * <p><b>结论：SHALL NOT 替换</b>。2026-10-15 实测发现两者产出的<b>键集不一致</b>：
 * fastjson2 按 <b>getter</b> 命名（{@code basePackage} / {@code className} / {@code dashedCaseName}），
 * hutool 按<b>字段</b>命名（{@code base_package} / {@code class_name} / {@code dashed_case_name}）。
 * 外部模板仓 {@code aicode_template} 的 {@code .ftl} / {@code .btl} 按 getter 名取值
 * （{@code ${className}}、{@code ${dashedCaseName}}），换成 hutool 会让<b>所有模板变量取不到值</b> ——
 * 编译与既有测试都可能仍然通过，而生成产物已静默改变。
 *
 * <p>本测试把该结论固化为可执行断言：只要有人再次尝试替换，测试即失败。
 *
 * @see openspec/changes/dead-code-and-dependency-hygiene/design.md - D5
 */
class TemplateDataExpansionTest {

    @Test
    @DisplayName("fastjson2 往返按 getter 名产出模板变量（basePackage / className / dashedCaseName）")
    void fastjsonRoundTrip_producesGetterNamedKeys() {
        TemplateData templateData = new TemplateData();
        templateData.setModel("order");
        templateData.setClassName("Order");
        templateData.setClassNameLower("order");
        templateData.setDashedCaseName("order");

        @SuppressWarnings("unchecked")
        Map<String, Object> param = JSON.parseObject(JSON.toJSONString(templateData), Map.class);

        assertThat(param)
                .as("外部模板仓按 getter 名取值，这几个键必须存在")
                .containsKeys("model", "className", "classNameLower", "dashedCaseName");
        assertThat(param.get("className")).isEqualTo("Order");
    }

    @Test
    @DisplayName("hutool BeanUtil.beanToMap 的键集与 fastjson2 往返不同 —— 故不可作为替代实现")
    void beanToMap_keysDifferFromFastjsonRoundTrip() {
        TemplateData templateData = new TemplateData();
        templateData.setModel("order");
        templateData.setClassName("Order");

        Map<String, Object> viaBeanUtil = BeanUtil.beanToMap(templateData, true, false);
        @SuppressWarnings("unchecked")
        Map<String, Object> viaFastjson = JSON.parseObject(JSON.toJSONString(templateData), Map.class);

        assertThat(viaFastjson.keySet())
                .as("getter 名键集（模板实际依赖的形态）")
                .contains("className");
        assertThat(viaBeanUtil.keySet())
                .as("hutool 产出的是字段名键集")
                .doesNotContain("className")
                .contains("class_name");

        assertThat(viaBeanUtil.keySet())
                .as("两者键集不一致，是保留 fastjson2 往返的直接原因")
                .isNotEqualTo(viaFastjson.keySet());
    }

    @Test
    @DisplayName("hutool 的 isIgnoreNull 参数不改变 null 键的存在性，不足以复刻 fastjson2 的省略语义")
    void beanToMap_nullHandlingDiffersFromFastjson() {
        TemplateData templateData = new TemplateData();
        templateData.setModel("order");

        @SuppressWarnings("unchecked")
        Map<String, Object> viaFastjson = JSON.parseObject(JSON.toJSONString(templateData), Map.class);

        assertThat(viaFastjson)
                .as("fastjson2 默认省略 null 字段")
                .doesNotContainKey("author");
        assertThat(BeanUtil.beanToMap(templateData, true, false))
                .as("hutool 即使 isIgnoreNull=true，键集合仍包含值为 null 的属性")
                .containsKey("author");
    }
}
