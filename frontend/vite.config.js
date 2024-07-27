import { fileURLToPath, URL } from 'node:url'

import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

const env = loadEnv(mode, process.cwd(), '');

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools()
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    // port: 8081
    port: 5173

  },
  define : {
    __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: 'false',
    global : 'window',
    'process.env.SOME_KEY': JSON.stringify(env.SOME_KEY)
  }
})

