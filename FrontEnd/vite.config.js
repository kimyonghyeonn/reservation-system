import legacy from "@vitejs/plugin-legacy";
import vue from "@vitejs/plugin-vue";
import path from "path";
import { defineConfig } from "vite";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue({
      template: {
        compilerOptions: {
          // ✅ ion-input-password-toggle을 커스텀 엘리먼트로 인식하게 설정
          isCustomElement: (tag) => tag === "ion-input-password-toggle",
        },
      },
    }),

    legacy(),
  ],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  test: {
    globals: true,
    environment: "jsdom",
  },
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
  build: {
    minify: "terser", // 기본값은 esbuild인데 terser로 변경
    terserOptions: {
      compress: {
        drop_console: true, // 모든 console.log, warn, error 제거
        drop_debugger: true, // debugger 문 제거
      },
    },
  },
});
