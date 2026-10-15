package com.aicode.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * Jackson 序列化定制 —— 把 {@code Long} / {@code long} 序列化为 JSON <b>字符串</b>。
 *
 * <h3>为什么必须这样做</h3>
 * 业务编码是 19 位雪花值（如 {@code 1882812765213425664}），远超 JavaScript 的安全整数上限
 * {@code Number.MAX_SAFE_INTEGER = 9007199254740992}。以 JSON 数字输出会被前端
 * {@code JSON.parse} 静默截断成 {@code 1882812765213425600} —— 末位被篡改，且无任何报错。
 * 前端是 Angular 7 + jQuery，<b>没有 BigInt</b>，无法在客户端补救，因此服务端必须返回字符串。
 *
 * <p>历史说明：本类此前注册的是 Jackson 2 的 {@code ObjectMapper} bean
 * （基于 {@code Jackson2ObjectMapperBuilder}）。Spring Boot 4 默认序列化库已切换为
 * Jackson 3（{@code tools.jackson}），那个 bean 不再被 HTTP 转换链路使用，
 * 注册会<b>静默失效</b>。现改为实现 {@link JsonMapperBuilderCustomizer} ——
 * 它作用于默认序列化链路本身，不可能出现「注册成功但没生效」。</p>
 *
 * <p>契约由 {@code openspec/specs/api-contract/spec.md} 定义，
 * 由 {@code ApiContractIT} 场景①守护。</p>
 */
@Configuration
public class WebDataConvertConfig {

    /**
     * 注册 Long → String 的序列化规则。
     *
     * <p>{@code Long.class}（装箱）与 {@code Long.TYPE}（基本类型）必须<b>都</b>注册 ——
     * 只注册前者会漏掉所有声明为 {@code long} 的字段。</p>
     */
    @Bean
    @ConditionalOnMissingBean(JsonMapperBuilderCustomizer.class)
    public JsonMapperBuilderCustomizer longToStringCustomizer() {
        return builder -> {
            SimpleModule module = new SimpleModule();
            module.addSerializer(Long.class, ToStringSerializer.instance);
            module.addSerializer(Long.TYPE, ToStringSerializer.instance);
            builder.addModules(module);
        };
    }
}
