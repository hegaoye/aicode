# 性能基线采集

两个脚本产出**结构化 JSON**，用于跨版本比较。升级前后各跑一次，逐字段 diff 即可判断
某项指标是变好、变坏还是基本持平。

| 脚本 | 测量对象 |
|------|---------|
| `startup-time.sh` | 冷启动耗时：停止已有实例 → `bootRun` → 轮询 `/actuator/health/liveness` 直到 200 |
| `metaspace-after-up.sh` | 启动后的内存占用：liveness 就绪后再等 `SETTLE_SEC`，读 `/actuator/metrics` |

## 用法

```bash
# 采集当前 JAVA_HOME 下的运行时
./measurement/startup-time.sh --samples 3 --output /tmp/after.json

# 指定被测 JDK
JAVA_HOME=/path/to/corretto-25 ./measurement/startup-time.sh --samples 3 --output /tmp/j25.json

# 比较两份基线
diff <(jq -S 'del(.measuredAt,.buildId)' /tmp/before.json) \
     <(jq -S 'del(.measuredAt,.buildId)' /tmp/after.json)
```

## 输出结构

两个脚本共用同一套标识字段，可直接对齐比较：

```json
{
  "javaVersion": "21.0.12.1",
  "javaVendor": "Amazon.com Inc.",
  "buildId": "5d987d8",
  "measuredAt": "2026-10-15T06:00:07Z",
  "startup":    { "livenessReadyMs": 7203, "...": "仅 startup-time.sh" },
  "metaspace":  { "usedBytes": 91776712, "...": "仅 metaspace-after-up.sh" }
}
```

## 统计口径

- 每个指标采样 **N 次取中位数**（默认 3，`--samples` 覆盖）。取中位数而非均值，是为了削弱
  单次 GC 与文件系统缓存带来的长尾影响。
- `startup-time.sh` 的耗时**包含 Gradle 启动与依赖装配**，不是纯 JVM 启动时间 —— 这是刻意的：
  该指标要反映的是"开发者实际等待多久"，而非 JVM 微观性能。
- 输出的 `samples` / `statistic` 字段记录了当时的采样口径，保证结果可复现与可质疑。

## 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `JAVA_HOME` | PATH 中的 java | 指定被测 JDK |
| `SAMPLES` / `--samples` | 3 | 采样次数 |
| `READY_TIMEOUT_SEC` | 120 | 等待 liveness 的上限秒数 |
| `SETTLE_SEC` | 10 | liveness 就绪后额外等待（仅 metaspace 脚本） |
| `LIVENESS_URL` | `http://127.0.0.1:8088/actuator/health/liveness` | liveness 端点 |
| `METRICS_URL` | `http://127.0.0.1:8088/actuator/metrics` | metrics 端点（仅 metaspace 脚本） |

## 已知限制

- **跨代指标口径差异**：Micrometer ≥ 1.13 废弃了 `jvm.memory.metaspace.*` 系列，改为按 `area`
  维度聚合。脚本会自动回退到 `jvm.memory.used?tag=area:nonheap`，但 `area:nonheap` 是「非堆总量」
  （含 metaspace + code cache 等），与旧的 `metaspace.used` **不完全等价**。跨 Micrometer 大版本
  比较时需在结论中注明这一口径差异。
- **依赖 Gradle 冷启动**：首次运行需下载 Gradle 发行版，耗时显著偏长。建议先跑一次预热。
- **macOS 兼容**：BSD `date` 不支持 `%3N`，脚本统一用 Python 取毫秒时间戳。
- 脚本会 `pkill` 正在运行的 `bootRun` / 应用进程，请勿在调试时并发执行。
