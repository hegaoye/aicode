interface Props {
  /**
   * 当前已知的、但暂未被注册的 `displayType`。用于 UI 提示，方便在添加
   * 新控件时一目了然地知道"这一类还需要支持"。
   */
  displayType?: string
}

/**
 * 未知 `displayType` 时的安全降级 —— 渲染一个禁用的输入框，并附
 * `display-control--unknown` 类名供视觉提示。
 *
 * 业务页面 SHALL NOT 直接引用；仅由 `<DisplayControl>` 在表查不到时调用。
 */
export default function UnknownControl({ displayType }: Props) {
  return (
    <input
      type="text"
      disabled
      className="display-control--unknown"
      title={displayType ? `暂未支持的控件类型：${displayType}` : '暂未支持的控件类型'}
      placeholder="—"
    />
  )
}
