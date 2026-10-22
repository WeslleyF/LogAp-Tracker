import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';

// Em dev, /api vai para a API local, igual ao proxy do nginx em produção (sem CORS).
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': process.env.VITE_API_URL ?? 'http://localhost:8080',
    },
  },
});
