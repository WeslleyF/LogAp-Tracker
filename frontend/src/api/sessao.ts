import type { Sessao } from '../types';

const CHAVE = 'logitrack.sessao';

let emMemoria: Sessao | null = null;

/** Guarda a sessão em memória e no localStorage (sobrevive ao recarregar a página). */
export const sessaoStore = {
  ler(): Sessao | null {
    if (emMemoria) return emMemoria;
    try {
      const bruto = localStorage.getItem(CHAVE);
      if (!bruto) return null;
      const sessao = JSON.parse(bruto) as Sessao;
      if (new Date(sessao.expiraEm).getTime() <= Date.now()) {
        localStorage.removeItem(CHAVE);
        return null;
      }
      emMemoria = sessao;
      return sessao;
    } catch {
      return null;
    }
  },

  salvar(sessao: Sessao) {
    emMemoria = sessao;
    try {
      localStorage.setItem(CHAVE, JSON.stringify(sessao));
    } catch {
      // sem localStorage (modo privado): a sessão vale só até recarregar
    }
  },

  limpar() {
    emMemoria = null;
    try {
      localStorage.removeItem(CHAVE);
    } catch {
      // nada a limpar
    }
  },
};
