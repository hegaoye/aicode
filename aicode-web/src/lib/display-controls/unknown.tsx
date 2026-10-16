interface Props {
  /**
   * 当前已知的、但暂未被注册的 `displayType`。用于 UI 提示，方便在添加
   * 新控件时一目了然地知道"这一类还需要支持"。
   */
  displayType?: string
  /**
   * 该值是否在**契约枚举**内。
   *
   * <p>用于区分两种「未命中」（delta spec 要求降级标识可见、可辨）：</p>
   * <ul>
   *   <li>`true` —— 后端 `DisplayAttributeDisplayType` 里有这个值，但前端尚未实现
   *       对应控件。属于**平台能力缺口**，补一个控件即可；</li>
   *   <li>`false` —— 连契约枚举里都没有，属脏数据或历史遗留。属于**数据问题**。</li>
   * </ul>
   */
  isKnownEnum?: boolean
}

/**
 * 未知 `displayType` 时的安全降级 —— 渲染一个禁用的输入框，并附
 * `display-control--unknown` 类名供视觉提示。
 *
 * 业务页面 SHALL NOT 直接引用；仅由 `<DisplayControl>` 在表查不到时调用。
 */
export default function UnknownControl({ displayType, isKnownEnum = false }: Props) {
  const label = displayType || '（未设置）'
  const reason = isKnownEnum
    ? '平台暂未支持该控件类型'
    : '未知控件类型（不在契约枚举内）'
  return (
    <input
      type="text"
      disabled
      className="display-control--unknown"
      data-testid="display-control-unknown"
      data-known-enum={isKnownEnum ? 'true' : 'false'}
      title={`${reason}：${label}`}
      placeholder="—"
    />
  )
}