import { useParams } from 'react-router-dom'

/**
 * `ProjectPlaceholderPage` —— M3 详情页待接入的占位。
 *
 * 当前实现：从 URL 取出 `:code` 渲染于提示文字中，不触发任何后端调用。
 *
 * 见 `openspec/changes/2026-10-17-login-and-project-list-pages/design.md`
 * Decision D7：M2 第 2 阶段不接详情页，仅放占位让"列表 → 详情"链接通路
 * 完整可演示（路由 + 参数传递），告诉协作者"该路由待 M3 接入"。
 */
export default function ProjectPlaceholderPage() {
  const params = useParams<{ code: string }>()

  return (
    <section>
      <h1>项目详情</h1>
      <p>
        项目编码：<code data-testid="project-placeholder-code">{params.code ?? '—'}</code>
      </p>
      <p className="muted">M3 详情页待接入（占位页）</p>
    </section>
  )
}
