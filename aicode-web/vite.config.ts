import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'

// 说明：
// - build.outDir 指向 build/dist；aicode-web 的 Jar 任务会把该目录内容以 static/ 为前缀打包，
//   Spring Boot 随后从 classpath:/static/ 提供页面（见 build.gradle 与 design.md - D4）。
// - dev server 的 proxy 把 /api 与 /websocket 转发到后端，避免开发期引入 CORS。
export default defineConfig({
  plugins: [react()],
  base: '/',
  build: {
    outDir: 'build/dist',
    emptyOutDir: true,
  },
  server: {
    port: 5173,
    proxy: {
      '/project': 'http://127.0.0.1:8080',
      '/framework': 'http://127.0.0.1:8080',
      '/displayAttribute': 'http://127.0.0.1:8080',
      '/login': 'http://127.0.0.1:8080',
      '/account': 'http://127.0.0.1:8080',
      '/logs': 'http://127.0.0.1:8080',
      '/res': 'http://127.0.0.1:8080',
      '/upload': 'http://127.0.0.1:8080',
      '/v3/api-docs': 'http://127.0.0.1:8080',
      '/swagger-ui': 'http://127.0.0.1:8080',
      '/websocket': { target: 'ws://127.0.0.1:8080', ws: true },
    },
  },
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: ['./src/test-setup.ts'],
  },
})
