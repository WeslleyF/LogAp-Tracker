import { http } from './http';
import type { Page } from '../types';

export interface ParamsLista {
  /** Base zero, como na API. */
  page: number;
  size: number;
  sort?: string;
}

export interface CrudApi<T> {
  recurso: string;
  listar: (params: ParamsLista) => Promise<Page<T>>;
  criar: (payload: unknown) => Promise<T>;
  atualizar: (id: number, payload: unknown) => Promise<T>;
  excluir: (id: number) => Promise<void>;
}

/** Cliente REST do contrato padrão do CrudController: `/api/<recurso>`. */
export function createCrudApi<T>(recurso: string): CrudApi<T> {
  return {
    recurso,
    listar: async (params) => (await http.get<Page<T>>(`/${recurso}`, { params })).data,
    criar: async (payload) => (await http.post<T>(`/${recurso}`, payload)).data,
    atualizar: async (id, payload) => (await http.put<T>(`/${recurso}/${id}`, payload)).data,
    excluir: async (id) => {
      await http.delete(`/${recurso}/${id}`);
    },
  };
}
