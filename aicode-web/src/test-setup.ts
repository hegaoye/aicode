import '@testing-library/jest-dom/vitest'
import { afterAll, afterEach, beforeAll } from 'vitest'
import { setupServer } from 'msw/node'
import {
  // 当前 M1 关心
  getList5MockHandler,
  // M2-1 + M2-2 关心
  getList2MockHandler,
  getSigninMockHandler,
  // 邻居端点：避免后续页面测试忘注册时"穿透到真实网络"
  getListMockHandler,
  getList1MockHandler,
  getList3MockHandler,
  getList4MockHandler,
  getList6MockHandler,
} from './generated/api.msw'

/**
 * MSW 服务 —— 让 Vitest 进程内所有未在测试里 `server.use(...)` 覆盖的
 * 请求都被 `api.msw.ts` 内由 orval 生成的 handlers 拦截。
 *
 * 选用 `msw/node` 是因为测试运行在 jsdom 之中，handler 在 Node 进程层面
 * 拦截而非浏览器层。本仓库不引入浏览器侧 `setupWorker` —— Vite dev 联调
 * 直接走 vite proxy（详见 `vite.config.ts`）。
 *
 * 新增端点接入测试时请同时在本文件中追加对应 handler。
 */
export const server = setupServer(
  // M1 关心的端点
  getList5MockHandler(),
  // M2-1 + M2-2 关心的端点
  getList2MockHandler(),
  getSigninMockHandler(),
  // 邻居端点：避免后续页面测试忘注册时"穿透到真实网络"
  getListMockHandler(),
  getList1MockHandler(),
  getList3MockHandler(),
  getList4MockHandler(),
  getList6MockHandler(),
)

beforeAll(() => server.listen())
afterEach(() => server.resetHandlers())
afterAll(() => server.close())
