import axios from 'axios';
import { sessaoStore } from './sessao';

let aoExpirar: (() => void) | null = null;

/** O AuthContext registra aqui o que fazer quando a API responde 401. */
export function registrarAoExpirar(callback: () => void) {
  aoExpirar = callback;
}

export const http = axios.create({ baseURL: '/api' });

http.interceptors.request.use((config) => {
  const token = sessaoStore.ler()?.token;
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

http.interceptors.response.use(
  (resposta) => resposta,
  (erro) => {
    const ehLogin = erro.config?.url?.includes('/auth/login');
    if (erro.response?.status === 401 && !ehLogin) {
      sessaoStore.limpar();
      aoExpirar?.();
    }
    return Promise.reject(erro);
  },
);
