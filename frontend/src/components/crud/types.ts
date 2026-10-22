import type { ReactNode } from 'react';
import type { DefaultValues, FieldValues } from 'react-hook-form';
import type { ZodType, ZodTypeDef } from 'zod';
import type { CrudApi } from '../../api/crudApi';

export type Formato = 'data' | 'datahora' | 'km' | 'moeda';

export interface CrudColuna<T> {
  campo: keyof T & string;
  titulo: string;
  formato?: Formato;
  render?: (item: T) => ReactNode;
}

export type TipoCampo =
  | 'text'
  | 'password'
  | 'number'
  | 'date'
  | 'datetime'
  | 'select'
  | 'veiculo'
  | 'cidade'
  | 'boolean';

export interface CrudCampo<F extends FieldValues> {
  nome: keyof F & string;
  rotulo: string;
  tipo: TipoCampo;
  /** Opções do `select`. */
  opcoes?: { value: string; label: string }[];
  /** Valores sugeridos de um `text`, que continua aceitando qualquer outro. */
  sugestoes?: string[];
  /** Casas decimais do `number` (padrão 0). */
  decimais?: number;
  obrigatorio?: boolean;
  placeholder?: string;
}

/**
 * Tudo que uma tela de CRUD precisa. `T` é o que a API devolve, `F` o que o formulário edita
 * (datas como string ISO, números como `number | ''`, veículo como id).
 */
export interface CrudConfig<T extends { id: number }, F extends FieldValues> {
  titulo: string;
  /** Singular, para os títulos "Novo ..." e "Editar ...". */
  singular: string;
  api: CrudApi<T>;
  schema: ZodType<F, ZodTypeDef, F>;
  colunas: CrudColuna<T>[];
  campos: CrudCampo<F>[];
  /** Campos numéricos obrigatórios começam sem valor (indefinidos), não com 0. */
  valoresIniciais: DefaultValues<F>;
  paraForm: (item: T) => F;
  /** Ordenação padrão da lista, no formato do Spring: `campo,asc|desc`. */
  ordenacao?: string;
  /** Aviso mostrado na confirmação de exclusão. */
  avisoExclusao?: string;
}
