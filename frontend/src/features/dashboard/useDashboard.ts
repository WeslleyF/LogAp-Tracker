import { keepPreviousData, useQuery } from '@tanstack/react-query';
import { http } from '../../api/http';
import type { ResumoDashboard } from '../../types';

/** As cinco métricas de uma vez; muda quando o veículo ou a data inicial mudam. */
export function useDashboard(veiculoId: number | null, dataInicial: string) {
  return useQuery({
    queryKey: ['dashboard', veiculoId, dataInicial],
    queryFn: async () =>
      (
        await http.get<ResumoDashboard>('/dashboard/resumo', {
          params: { veiculoId: veiculoId ?? undefined, dataInicial },
        })
      ).data,
    placeholderData: keepPreviousData,
  });
}
