#!/usr/bin/env bash
#
# metaspace-after-up.sh —— 应用启动后的内存占用基线采集
#
# 测量方式：复用 startup-time.sh 的启动与就绪判定，在 liveness 返回 200 之后再等
#           SETTLE_SEC 秒（默认 10，让类加载与 JIT 趋于稳定），读取 /actuator/metrics。
# 输出：结构化 JSON，common-fields 与 startup-time.sh 完全一致。
#
# 采样方法：每个指标采样 N 次取中位数（默认 N=3）。读取的是运行中 JVM 的实时指标，
#           单次读取的抖动远小于启动耗时，但仍取中位数以削弱 GC 周期的影响。
#
# common-fields（与 startup-time.sh 完全一致）：
#   javaVersion  被测运行时的 java -version 版本串，如 "21.0.12.1"
#   javaVendor   JVM 供应商
#   buildId      当前 git commit（用于关联代码版本）
#   measuredAt   采集时刻（ISO-8601, UTC）
#
# 注意：application-test.yml 把 actuator 端点全部关闭，但本脚本测的是**生产** profile，
#       其中 management.endpoints.web.exposure.include="*"，故 /actuator/metrics 可访问。
#
# usage:
#   ./measurement/metaspace-after-up.sh [--output <file.json>] [--samples N]
#
# env:
#   JAVA_HOME            指定被测 JDK
#   SETTLE_SEC           liveness 就绪后额外等待秒数，默认 10
#   READY_TIMEOUT_SEC    等待 liveness 的上限秒数，默认 120
#   LIVENESS_URL         liveness 端点地址
#   METRICS_URL          metrics 端点地址

set -uo pipefail

# 毫秒时间戳：macOS 的 BSD date 不支持 %3N（会输出字面量 N），
# 因此统一走 Python，保证跨 Linux / macOS 结果一致且可比。
now_ms() { python3 -c 'import time; print(int(time.time() * 1000))'; }

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUTPUT=""
SAMPLES=3
SETTLE_SEC="${SETTLE_SEC:-10}"
READY_TIMEOUT_SEC="${READY_TIMEOUT_SEC:-120}"
LIVENESS_URL="${LIVENESS_URL:-http://127.0.0.1:8088/actuator/health/liveness}"
METRICS_URL="${METRICS_URL:-http://127.0.0.1:8088/actuator/metrics}"
BOOT_LOG="${BOOT_LOG:-/tmp/aicode-metaspace.log}"

while [ $# -gt 0 ]; do
  case "$1" in
    --output)  OUTPUT="${2:-}"; shift 2 ;;
    --samples) SAMPLES="${2:-3}"; shift 2 ;;
    -h|--help) sed -n '2,28p' "${BASH_SOURCE[0]}"; exit 0 ;;
    *) echo "未知参数：$1" >&2; exit 2 ;;
  esac
done

if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ]; then
  JAVA_BIN="$JAVA_HOME/bin/java"
else
  JAVA_BIN="$(command -v java)"
fi
[ -x "$JAVA_BIN" ] || { echo "找不到 java 可执行文件" >&2; exit 3; }

# ---- common-fields ----
# java -version 的输出格式在不同发行版间不一致：
# 版本号取 `java -version` 首行的第 3 个空白分隔字段并剥掉引号；
# vendor 则不猜 —— `java -version` 首行在不同发行版下结构不一，直接读
# `-XshowSettings:properties` 的 java.vendor 属性。
_version_line="$("$JAVA_BIN" -version 2>&1 | head -1)"
java_version="$(echo "$_version_line" | awk '{print $3}' | tr -d '"')"
java_vendor="$("$JAVA_BIN" -XshowSettings:properties -version 2>&1 \
  | sed -nE 's/^[[:space:]]*java\.vendor[[:space:]]*=[[:space:]]*(.*)$/\1/p' \
  | head -1 | tr -d '\r')"
[ -n "$java_vendor" ] || java_vendor="unknown"
build_id="$(cd "$REPO_ROOT" && git rev-parse --short HEAD 2>/dev/null || echo unknown)"
measured_at="$(date -u +%Y-%m-%dT%H:%M:%SZ)"

stop_existing() {
  pkill -f "com.aicode.Application" 2>/dev/null
  pkill -f "GradleWrapperMain" 2>/dev/null
  sleep 3
}

wait_ready() {
  local waited=0
  while [ "$waited" -lt "$READY_TIMEOUT_SEC" ]; do
    sleep 1; waited=$((waited + 1))
    if [ "$(curl -s -o /dev/null -w '%{http_code}' --max-time 2 "$LIVENESS_URL" 2>/dev/null)" = "200" ]; then
      return 0
    fi
  done
  return 1
}

# 读取 /actuator/metrics/<name> 的 measurements[0].value
read_metric() {
  curl -s --max-time 5 "$METRICS_URL/$1" 2>/dev/null | python3 -c "
import sys, json
try:
    m = (json.load(sys.stdin).get('measurements') or [])
    print(int(m[0]['value']) if m else '')
except Exception:
    print('')
" 2>/dev/null
}

# Metaspace 读取需兼容 Micrometer 的两代指标命名：
#   旧（≤1.12）：jvm.memory.metaspace.used / .committed / .max
#   新（≥1.13）：按 area 维度聚合，metaspace 归入 jvm.memory.used?tag=area:nonheap
# 实测 Spring Boot 3.5.16（Micrometer 1.17.x）只提供后者。
# 因此优先用旧名（语义精确），404 时回退到 area:nonheap。
# 注意：area:nonheap 是「非堆」总量，包含 metaspace + code cache + 其他，
#       与旧的 metaspace.used 不完全等价，跨代比较时需注明口径差异。
read_metaspace_metric() {
  local kind="$1" val
  val="$(read_metric "jvm.memory.metaspace.$kind")"
  if [ -z "$val" ]; then
    val="$(read_metric "jvm.memory.$kind?tag=area:nonheap")"
  fi
  echo "$val"
}

median_of() { printf '%s\n' "$@" | sort -n | awk -v n="$#" 'NR==int((n+1)/2)'; }

metaspace_used=()
metaspace_committed=()
heap_used=()
i=1
while [ "$i" -le "$SAMPLES" ]; do
  stop_existing
  ( cd "$REPO_ROOT/aicode" && nohup ./gradlew :aicode:bootRun --console=plain >"$BOOT_LOG" 2>&1 & )

  if wait_ready; then
    sleep "$SETTLE_SEC"
    mu="$(read_metaspace_metric used)"
    mc="$(read_metaspace_metric committed)"
    hu="$(read_metric 'jvm.memory.used?tag=area:heap')"
    if [ -n "$mu" ]; then
      metaspace_used+=("$mu")
      [ -n "$mc" ] && metaspace_committed+=("$mc")
      [ -n "$hu" ] && heap_used+=("$hu")
      echo "  样本 $i/$SAMPLES: nonheap/metaspace.used=${mu}B committed=${mc:-N/A} heap.used=${hu:-N/A}" >&2
    else
      echo "  样本 $i/$SAMPLES: 读取 $METRICS_URL 失败（端点未暴露？），作废" >&2
    fi
  else
    echo "  样本 $i/$SAMPLES: 启动超时（>${READY_TIMEOUT_SEC}s），作废" >&2
  fi
  i=$((i + 1))
done

stop_existing

if [ ${#metaspace_used[@]} -eq 0 ]; then
  echo "所有样本均失败，无法采集基线。日志见 $BOOT_LOG" >&2
  exit 4
fi

mu_med=$(median_of "${metaspace_used[@]}")
mc_med="null"; [ ${#metaspace_committed[@]} -gt 0 ] && mc_med=$(median_of "${metaspace_committed[@]}")
hu_med="null"; [ ${#heap_used[@]} -gt 0 ] && hu_med=$(median_of "${heap_used[@]}")

json=$(cat <<JSON
{
  "javaVersion": "$java_version",
  "javaVendor": "$java_vendor",
  "buildId": "$build_id",
  "measuredAt": "$measured_at",
  "metaspace": {
    "usedBytes": $mu_med,
    "committedBytes": $mc_med,
    "maxBytes": null,
    "metricNameUsed": "jvm.memory.metaspace.used 或 jvm.memory.used?tag=area:nonheap（自动回退）",
    "heapUsedBytes": $hu_med,
    "settleSec": $SETTLE_SEC,
    "metricsUrl": "$METRICS_URL",
    "samples": ${#metaspace_used[@]},
    "statistic": "median"
  }
}
JSON
)

echo "$json"
if [ -n "$OUTPUT" ]; then
  printf '%s\n' "$json" > "${OUTPUT}"
  echo "(written to ${OUTPUT})" >&2
fi
exit 0
