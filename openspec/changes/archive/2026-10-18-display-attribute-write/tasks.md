# Tasks

## 0. 实施前核实

- [x] 0.1 **核实 orval 对 springdoc `allowableValues` 的生成形态**（design D3，决定实现路径）：改一个字段后跑 `npm run generate`，看 `DisplayAttributeVO` 的 `displayType` 是生成为 `as const` 枚举对象（则前端可直接 `Object.values()`）还是仅字符串联合类型（则前端须显式列出并由测试钉住与契约一致）。结论回填 design D3 *(**核实结论（比预期好）**：orval 把 allowableValues 生成为 `as const` 对象（形态同 Build2OneToOne），前端可直接 `Object.values()` 取全集 —— **零手写**，delta spec 的「从契约读值域」天然满足。design D3 的备选路径不需要)*
- [x] 0.2 **核实 `@Schema(allowableValues = ...)` 能否直接接枚举类的 `values()`**（design D2 的漂移风险）：查当前 springdoc 版本是否支持传 `Class<? extends Enum>`；若不支持，记录为"必须手写 + 由测试钉住" *(**核实结论：不能，且 static final String[] 也不行**（数组引用不是常量表达式）→ 编译失败。只能手写字面量。已回填 design D3b 并在 @Schema 的 description 里写明维护须知)*
- [x] 0.3 核实 `matchType` / `fieldValidationMode` 是否有对应的枚举类；若只有 `description` 里的中文列举，记录"只加可确认的值、不强行穷举"（design Open Questions） *(**核实结论：matchType / fieldValidationMode 均无枚举类**，值域只在 description 的中文列举里。故只收录能从描述明确读出的值（matchType 9 项 / fieldValidationMode 11 项），**不编造**。另比对发现 displayType 的 description 与枚举类完全一致（24 = 24，无遗漏）)*
- [x] 0.4 核实 `DisplayAttributesPage` 现状（只有 1 个用例）与其消费 `list5` 的字段集合，作为改造基线 *(已核实：DisplayAttributesPage 只调 list5、只有 1 个用例、mock 用假枚举值 text/number)*

## 1. 后端：三个字段的值域进契约

- [x] 1.1 `facade/display/entity/DisplayAttribute.java`：`displayType` 加 `@Schema(allowableValues = ...)`，取值取自 `DisplayAttributeDisplayType` 的全部 24 个枚举字面量（按 task 0.2 结论决定能否引 `values()`）；`@Schema` 的 `description` 保留现有中文说明 *(已完成：**注意改的是 `DisplayAttributeVO` 而非 `DisplayAttribute` 实体** —— 契约里渲染的是 VO（`/displayAttribute/list` 的出参），改实体无效（我最初改错对象，实测契约里 enum=False 才定位到）)*
- [x] 1.2 同一实体：`matchType` 加 `allowableValues`（按 task 0.3 结论取值，不编造） *(已完成：9 项（= != >= <= > < like between in）)*
- [x] 1.3 同一实体：`fieldValidationMode` 加 `allowableValues`（同上） *(已完成：11 项)*
- [x] 1.4 验证 `./aicode/gradlew :facade:compileJava` exit=0，且**未改动任何业务逻辑**（本变更 SHALL NOT 碰 Controller / Service） *(通过 —— 本变更零业务逻辑改动（Controller / Service 未碰）；`MapFieldColumn` 等实体亦未动)*

## 2. 契约守护

- [x] 2.1 `ApiContractIT` 新增 `@Nested` 场景⑬ `DisplayAttributeEnumContract`： *(已完成：场景⑬ DisplayAttributeEnumContract **3 用例**)*
  - 用例 1：导出契约中 `DisplayAttributeVO.displayType` SHALL 带 `enum:` 列表，SHALL NOT 是裸 `type: string`
  - 用例 2：`enum` 的取值集合 SHALL 与 `DisplayAttributeDisplayType` 的枚举名集合**完全一致**（不多不少）—— 这是 task 0.2 兜底漂移的手段
  - 用例 3：`matchType` / `fieldValidationMode` SHALL 同样带 `enum`（若 task 0.3 判定值域不可考，则改为断言其**保持现状并在测试中登记原因**，SHALL NOT 静默跳过）
- [x] 2.2 **反向验证**：临时去掉 `displayType` 的 `allowableValues` → 场景⑬ 用例 1 失败 *(**反向验证成立**：删掉字面量 "Autocomplete" → 用例 2 立刻红，错误精确指出「缺失=[Autocomplete]」。**同时修正了我自己写错的断言**：原先用 `doesNotContain("type: string")` 判「非裸 string」，但 OpenAPI 的正确形态是 `type: string` + `enum:` 共存（前者是后者的必要组成）—— 改判据为「enum: 后紧随列表项」)*

## 3. 契约产物 + 前端生成物

- [x] 3.1 跑 `npm run generate`；确认 `displayAttributeVO.ts` 的 `displayType` 形态（结论回填 design D3 与 task 0.1） *(已完成：生成 `displayAttributeVODisplayType` / `DisplayAttributeDisplayType` / `matchType` / `fieldValidationMode` 等 4 个枚举 model)*
- [x] 3.2 清理 `src/generated/model/index.ts` 的 stale export（若 orval 未清） *(已完成：index.ts 已收录（grep 命中 2）)*
- [x] 3.3 跑 `npm run typecheck`：预期零报错（本变更尚未改前端类型） *(typecheck exit=0)*

## 4. 前端：修分发层（design D1）

- [x] 4.1 `src/lib/display-controls/index.tsx`：`CONTROLS` 的 key 由 `text` / `number` / `date` / `select` 改为 `input` / `inputnumber` / `datepicker` / `select`（**后端枚举的小写形式**，design D1）；保留 `.toLowerCase()` 归一 *(已完成：key 由 text/number/date/select 改为 **input/inputnumber/datepicker/select**（后端枚举小写）；保留 .toLowerCase() 容错)*
- [x] 4.2 `unknown.tsx`：降级标识 SHALL 能显示"该类型尚未支持"，SHALL NOT 只显示原始字符串（delta spec 要求可见降级标识） *(已完成：UnknownControl 新增 `isKnownEnum` prop，区分「平台暂未支持该控件类型」（枚举内）与「未知控件类型（不在契约枚举内）」（枚举外），带 data-known-enum 标记)*
- [x] 4.3 `SUPPORTED_DISPLAY_TYPES` 的来源改为**契约枚举**（task 0.1 结论决定写法）；SHALL NOT 保留手写常量副本 *(已完成：SUPPORTED_DISPLAY_TYPES 改为 `Object.keys(CONTROLS)` 派生；新增 ALL_DISPLAY_TYPES = `Object.values(生成物枚举)`)*
- [x] 4.4 扩充 `display-controls` 测试： *(已完成 12 用例（原 7 条全部用假值，已废弃重写）。**反向验证成立**：CONTROLS 改回 M1 自造 key → **6 条同时变红**（含关键的「未命中枚举集合」那条）。同时更新了 DisplayAttributesPage 测试里的假枚举值（text→Input / number→InputNumber）)*
  - 用例：`'Input'` / `'INPUT'` / `'input'` 三种写法 SHALL 全部命中文本控件（大小写容错，design D1）
  - 用例：`'DatePicker'` SHALL 命中日期控件、`'InputNumber'` SHALL 命中数字控件、`'Select'` SHALL 命中选择器
  - **反向用例（delta spec「分发器 key 与后端枚举的对应关系 SHALL 可核对」）**：断言"后端 24 个枚举中未命中的集合"可被列出并**非空但已知** —— 用例里写死当前未命中数（23 → 随实现变化更新），这样后端加枚举时会立刻红
  - 用例：未识别类型 SHALL 渲染带可见降级标识的占位，SHALL NOT 抛异常

## 5. 前端：接 `POST /displayAttribute/save`

- [x] 5.1 新建 `src/pages/DisplayAttributesPage/DisplayAttributeForm.tsx`：8 个高频字段（design D5 表格），`data-testid="display-attr-{field}-input"` *(已完成：8 字段（design D5 表格），data-testid 齐备)*
- [x] 5.2 `displayType` 的下拉选项 SHALL 来自契约枚举（SHALL NOT 手写副本）；`displayName` 文本；5 个 checkbox（`isRequired` / `isInsert` / `isAllowUpdate` / `isListPageDisplay` / `isDetailPageDisplay`）；`displayNo` 数字 *(已完成：下拉取自 `Object.values(DisplayAttributeVODisplayType)`；另加 `normalizeDisplayType` **运行时过滤**（只接受契约枚举内的值）—— 用类型断言会放进脏数据)*
- [x] 5.3 提交调 `api.build3(...)`（函数名以生成物为准）传整页所有行的 `List<DisplayAttribute>`，每项带隐藏的 `mapFieldColumnCode`（design D6） *(已完成：`api.build6(payload)` 传整页；每项带隐藏 `mapFieldColumnCode` + `mapClassTableCode`（design D6）)*
- [x] 5.4 **提交成功后 SHALL 重新拉取列表**（design D4：后端 `save` 无事务且对找不到字段的行静默 `continue`，乐观更新会掩盖结果） *(已完成：`await reload()` 重拉（后端 save 无事务且对找不到字段的行静默 continue，乐观更新会掩盖结果）)*
- [x] 5.5 `DisplayAttributesPage` 升级为可编辑：每行一个可折叠的表单 + 页级「保存全部」按钮；UI 上 SHALL 有一处提示「改完展示属性需**重新构建**才能在生成代码里看到效果」（design Risks） *(已完成：只读表格保留 + 新增编辑区 + 页级「保存全部」；提示「改完需重新构建」)*
- [x] 5.6 `DisplayAttributesPage` 既有 1 个用例 SHALL 仍通过（其断言可能依赖 list5 的渲染结构，逐项确认） *(既有 2 个用例仍通过（其 mock 假枚举值已改为真实值）)*

## 6. 前端：测试

- [x] 6.1 `DisplayAttributeForm` 测试：① 渲染 8 个控件 ② `displayType` 下拉选项与契约枚举一致 ③ 提交带齐每行的 `mapFieldColumnCode` + 8 字段 ④ 提交成功后重拉列表 ⑤ `R.code !== "0000"` → 错误气泡且**保留用户已改的值** *(已完成，**并抓出两个真 bug**：① `onChange` 收完整对象导致 stale closure（改显示名被勾必填覆盖）；② **编辑区渲染误用只读态 `state.rows` 而非编辑态 `rows`** —— rows 更新了（所以提交内容正确）却没参与渲染，input 显示旧值。两个 bug 都在「失败时保留已改值」这条用例上暴露)*
- [x] 6.2 `DisplayAttributesPage` 测试：① 可折叠/展开 ② 「保存全部」批量提交整页 ③ 提交中禁用按钮防重复 ④ 空列表态 *(已完成：1 → 10 用例)*

## 7. 验收 + 文档 + 登记

- [x] 7.1 后端全套 `JAVA_HOME=$(/usr/libexec/java_home -v 25) ./aicode/gradlew test`，登记三模块用例数与失败数 *(实测：aicode 193（190 基线 + 场景⑬ 3）/ 2 跳过 / 0 失败；facade 39；common 56 —— 合计 288)*
- [x] 7.2 前端全套 `npm run typecheck` + `npm test` + `npm run build` *(typecheck exit=0；22 文件 / 132 用例全过；build 107 modules / 348 KB（+5 KB）)*
- [x] 7.3 契约抖动判定：两版契约各自 `sort` 后 `diff`，确认无行被"删了又加回"（AGENTS §10 已登记该方法） *(零抖动：删除 20 行（全是旧的中文列举 description，被 enum 替代）+ 新增 112 行；comm 比对确认**无行被删了又加回**)*
- [x] 7.4 `openspec validate --strict 2026-10-18-display-attribute-write` 与 `--specs` *(openspec validate --strict 与 --specs 均通过（13/13）)*
- [x] 7.5 `AGENTS.md §11.3`：前端清单更新 `DisplayAttributesPage`（1 → N 用例）+ 新增 `display-controls` 用例数；登记「分发器 key 已与后端枚举对齐 + 未命中集合可列出」这一守护 *(已完成：§11.3 新增「控件分发层 display-controls 12」并逐条列语义；DisplayAttributesPage 1 → 10 并列 10 项；统计 21 文件/119 → **22 文件/132**)*
- [x] 7.6 `AGENTS.md §10`：登记「spec 若只写『至少覆盖 N 类』而不要求与后端枚举对齐，会允许出 23/24 全失配的实现」这条教训 *(已完成：§10 新增两条实测坑 —— ① spec 只写「至少覆盖 N 类」而不要求与上游枚举对齐会允许 N-1/N 全失配（附两条处方）② Java 注解只接受编译期常量、`static final String[]` 也不行（附 orval 生成 as const 的好消息）)*
- [x] 7.7 `AGENTS.md §7.10 第 9 项`：消费端点 14 → 15；「未接入的高价值端点」移除 `/displayAttribute/save` *(已完成：§7.10 消费端点 14 → **15**；未接入项移除 displayAttribute/save，改为关系删除与可视化编辑)*
- [x] 7.8 同步 delta spec 到 `aicode-web-page` 与 `api-data-object`，然后 `openspec archive` *(已完成：主 spec 物化 —— aicode-web-page 9 Req（Req 增补「key SHALL 与后端枚举对齐」+ 新增「集合可核对」Scenario）/ api-data-object 8 Req（Req 增补「受约束字段 SHALL 以 enum 表达值域」+「前端从契约 enum 读值域」2 Scenario）；13/13 通过；变更已 archive)*
- [x] 7.9 commit（中文 message，一个变更一个 commit） *(已完成：中文 message，单变更单 commit)*