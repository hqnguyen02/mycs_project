import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    watch:  {
      usePolling: true
    },
    host: true,
    port: 80,
    allowedHosts: ['csc342-519-host.csc.ncsu.edu', 'csc342-519.csc.ncsu.edu']
  }
})
