import vue from '@vitejs/plugin-vue'
import path from 'path'
import { defineConfig } from 'vite'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), tailwindcss()],
  // 别名
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {

  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData:
          `
        @import "@/assets/style/variables.scss";
        @import "@/assets/style/mixin.scss";
        `,
      }
    }
  }
})
