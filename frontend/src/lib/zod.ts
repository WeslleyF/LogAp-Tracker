import { z } from 'zod';

// Peças reaproveitadas pelos schemas dos formulários.

export const textoObrigatorio = (rotulo: string, max = 100) =>
  z.string().trim().min(1, `Informe ${rotulo}`).max(max, `Máximo de ${max} caracteres`);

/** Campo numérico obrigatório: vazio (string '') vira a mensagem de erro. */
export const numeroObrigatorio = (mensagem: string) =>
  z.number({ required_error: mensagem, invalid_type_error: mensagem });

/** Campo numérico opcional: aceita vazio ('') ou um número positivo. */
export const numeroPositivoOpcional = (mensagem: string) =>
  z.union([z.literal(''), z.number().positive(mensagem)]);

export const dataObrigatoria = (mensagem: string) => z.string().min(1, mensagem);
