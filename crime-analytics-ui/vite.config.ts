import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  define: {
    global: 'globalThis',
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 3000,
    proxy: {
      '/api/v1/cases': 'http://localhost:8082',
      '/api/v1/lookups': 'http://localhost:8082',
      '/api/v1/incidents': 'http://localhost:8082',
      '/api/v1/persons': 'http://localhost:8083',
      '/api/v1/graph': 'http://localhost:8084',
      '/api/v1/search': 'http://localhost:8085',
      '/api/v1/financial': 'http://localhost:8088',
      '/api/v1/reports': 'http://localhost:8089',
      '/api/v1/chat': 'http://localhost:8087',
      '/api/notifications': 'http://localhost:8090',
      '/ws': {
        target: 'http://localhost:8087',
        ws: true,
      },
    },
  },
  optimizeDeps: {
    include: ['leaflet'],
  },
});
