import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'
import tailwindcss from '@tailwindcss/vite'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
  ],

  root: '.',
  build: {
    outDir: "dist",
    assetsDir: "assets",
    sourcemap: false,
    rolldownOptions: {
      output: {
        dir: path.resolve(__dirname, 'dist'),
        entryFileNames: '[name].js',
        chunkFileNames: '[name].js',
        assetFileNames: '[name].[ext]'
      }
    }
  },

  server: {
    port: 9528,
    open: true,
    host: true,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:9527',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
  resolve: {
    alias: {
      '@views': path.resolve(__dirname, 'src/views'),
      "@type": path.resolve(__dirname, 'src/types'),
      "@api": path.resolve(__dirname, 'src/api'),
      "@redux": path.resolve(__dirname, 'src/redux'),
      "@css": path.resolve(__dirname, 'src/assets/style'),
      "@utils": path.resolve(__dirname, 'src/utils')
    }
  }
})
