import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import type { ReactNode } from 'react';
import { http, registrarAoExpirar } from '../../api/http';
import { sessaoStore } from '../../api/sessao';
import type { Sessao } from '../../types';

interface AuthValor {
  sessao: Sessao | null;
  ehAdmin: boolean;
  entrar: (email: string, senha: string) => Promise<void>;
  sair: () => void;
}

const AuthContext = createContext<AuthValor | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [sessao, setSessao] = useState<Sessao | null>(() => sessaoStore.ler());

  const sair = useCallback(() => {
    sessaoStore.limpar();
    setSessao(null);
  }, []);

  useEffect(() => registrarAoExpirar(sair), [sair]);

  const entrar = useCallback(async (email: string, senha: string) => {
    const { data } = await http.post<Sessao>('/auth/login', { email, senha });
    sessaoStore.salvar(data);
    setSessao(data);
  }, []);

  const valor = useMemo(
    () => ({ sessao, ehAdmin: sessao?.perfil === 'ADMIN', entrar, sair }),
    [sessao, entrar, sair],
  );

  return <AuthContext.Provider value={valor}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthValor {
  const contexto = useContext(AuthContext);
  if (!contexto) throw new Error('useAuth precisa estar dentro de AuthProvider');
  return contexto;
}
