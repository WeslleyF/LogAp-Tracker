import { isAxiosError } from 'axios';

/** Erro da API (ProblemDetail, RFC 7807) já normalizado para a tela. */
export interface Problema {
  status: number;
  mensagem: string;
  erros: Record<string, string>;
}

export function extrairProblema(erro: unknown): Problema {
  if (isAxiosError(erro)) {
    const dados = erro.response?.data as
      { detail?: string; erros?: Record<string, string> } | undefined;
    if (erro.response) {
      return {
        status: erro.response.status,
        mensagem: dados?.detail ?? 'Erro inesperado na API',
        erros: dados?.erros ?? {},
      };
    }
    return { status: 0, mensagem: 'Não foi possível falar com o servidor', erros: {} };
  }
  return { status: 0, mensagem: 'Erro inesperado', erros: {} };
}
