import { useQuery } from '@tanstack/react-query';
import { http } from '../../api/http';
import type { Page, Veiculo } from '../../types';

/** Veículos para o seletor de formulários e do dashboard. */
export function useVeiculos() {
  return useQuery({
    queryKey: ['veiculos', 'opcoes'],
    queryFn: async () =>
      (await http.get<Page<Veiculo>>('/veiculos', { params: { size: 200, sort: 'placa,asc' } }))
        .data.content,
  });
}

/** Cidades já usadas em viagens, para sugerir origem e destino. */
export function useCidades() {
  return useQuery({
    queryKey: ['viagens', 'cidades'],
    queryFn: async () => (await http.get<string[]>('/viagens/cidades')).data,
  });
}
