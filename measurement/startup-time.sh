#!/usr/bin/env bash
#
# startup-time.sh —— 冷启动耗时基线采集
#
# 测量方式：停掉已有实例 → 启动 bootRun → 轮询 /actuator/health/liveness 直到返回 200。
# 输出：结构化 JSON，可直接与另一版本的输出逐字段比对。
#
# 采样方法：每个指标采样 N 次取中位数（默认 N=3，可用 --samples 覆盖）。
#          取中位数而非均值，是为了削弱单次 GC 与文件��统缓存带来的长尾影响。
#
# common-fields（与 metaspace-after-up.sh 完全一致）：
#   javaVersion  被测运行时的 java -version 版本串，如 "21.0.12.1"
#   javaVendor   JVM 供应商
#   buildId      当前 git commit（用于关联代码版本）
#   measuredAt   采集时刻（ISO-8601, UTC）
#
# usage:
#   ./measurement/startup-time.sh [--output <file.json>] [--samples N]
#
# env:
#   JAVA_HOME            指定被测 JDK，缺省用 PATH 中的 java
#   READY_TIMEOUT_SEC    等待 liveness 的上限秒数，默认 120
#   LIVENESS_URL         liveness 端点地址

set -uo pipefail

# 毫秒时间戳：macOS 的 BSD date 不支持 %3N（会输出字面量 N），
# 因此统一走 Python，保证跨 Linux / macOS 结果一致且可比。
now_ms() { python3 -c 'import time; print(int(time.time() * 1000))'; }

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUTPUT=""
SAMPLES=3
READY_TIMEOUT_SEC="${READY_TIMEOUT_SEC:-120}"
LIVENESS_URL="${LIVENESS_URL:-http://127.0.0.1:8088/actuator/health/liveness}"
BOOT_LOG="${BOOT_LOG:-/tmp/aicode-startup-time.log}"

while [ $# -gt 0 ]; do
  case "$1" in
    --output)  OUTPUT="${2:-}"; shift 2 ;;
    --samples) SAMPLES="${2:-3}"; shift 2 ;;
    -h|--help) sed -n '2,22p' "${BASH_SOURCE[0]}"; exit 0 ;;
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

samples_ms=()
i=1
while [ "$i" -le "$SAMPLES" ]; do
  stop_existing
  ( cd "$REPO_ROOT/aicode" && nohup ./gradlew :aicode:bootRun --console=plain >"$BOOT_LOG" 2>&1 & )

  t0=$(now_ms)
  waited=0
  ready_ms=""
  while [ "$waited" -lt "$READY_TIMEOUT_SEC" ]; do
    sleep 1
    waited=$((waited + 1))
    if [ "$(curl -s -o /dev/null -w '%{http_code}' --max-time 2 "$LIVENESS_URL" 2>/dev/null)" = "200" ]; then
      ready_ms=$(( $(now_ms) - t0 ))
      break
    fi
  done

  if [ -n "$ready_ms" ]; then
    samples_ms+=("$ready_ms")
    echo "  样本 $i/$SAMPLES: liveness 就绪 ${ready_ms} ms" >&2
  else
    echo "  样本 $i/$SAMPLES: 启动超时（>${READY_TIMEOUT_SEC}s），作废" >&2
    tail -15 "$BOOT_LOG" >&2
  fi
  i=$((i + 1))
done

stop_existing

if [ ${#samples_ms[@]} -eq 0 ]; then
  echo "所有样本均超时，无法采集基线。日志见 $BOOT_LOG" >&2
  exit 4
fi

sorted=$(printf '%s\n' "${samples_ms[@]}" | sort -n)
median=$(echo "$sorted" | awk -v n=${#samples_ms[@]} 'NR==int((n+1)/2)')
min=$(echo "$sorted" | head -1)
max=$(echo "$sorted" | tail -1)

json=$(cat <<JSON
{
  "javaVersion": "$java_version",
  "javaVendor": "$java_vendor",
  "buildId": "$build_id",
  "measuredAt": "$measured_at",
  "startup": {
    "livenessReadyMs": $median,
    "livenessReadyMsMin": $min,
    "livenessReadyMsMax": $max,
    "livenessUrl": "$LIVENESS_URL",
    "readyTimeoutSec": $READY_TIMEOUT_SEC,
    "samples": ${#samples_ms[@]},
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
