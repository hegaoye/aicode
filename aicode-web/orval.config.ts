import { defineConfig } from 'orval'

/**
 * 由后端导出的契约生成前端的一切：类型、请求函数、MSW mock。
 *
 * 输入是入库的 contracts/openapi/openapi.yaml —— 它由 aicode 的 OpenApiExportIT
 * 在测试期刷新，因此「后端改字段 → 契约变 → 前端类型变 → 用了旧字段的代码编译失败」
 * 这条防漂移链才成立。
 *
 * 三条硬约定：
 *   1. 生成物一律落在 src/generated/，与手写代码物理分开，review 时一眼可辨；
 *   2. 生成物入库，以便无 node 环境也能做类型检查；
 *   3. 升级 orval 时 SHALL 重跑并 review 差异 —— 生成格式与 orval 版本绑定。
 */
export default defineConfig({
  aicode: {
    input: '../contracts/openapi/openapi.yaml',
    output: {
      target: 'src/generated/api.ts',
      schemas: 'src/generated/model',
      mode: 'single',
      client: 'axios',
      // orval 8.x 的 mock 配置为 generators 数组
      mock: {
        generators: [{ type: 'msw' }],
      },
    },
  },
})
