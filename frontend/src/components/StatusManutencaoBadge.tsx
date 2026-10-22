import { Badge } from '@mantine/core';
import type { StatusManutencao } from '../types';

const CORES: Record<StatusManutencao, string> = {
  PENDENTE: 'yellow',
  EM_REALIZACAO: 'blue',
  CONCLUIDA: 'green',
  REJEITADA: 'gray',
};

export const ROTULOS_STATUS: Record<StatusManutencao, string> = {
  PENDENTE: 'Pendente',
  EM_REALIZACAO: 'Em realização',
  CONCLUIDA: 'Concluída',
  REJEITADA: 'Rejeitada',
};

export function StatusManutencaoBadge({ status }: { status: StatusManutencao }) {
  return (
    <Badge color={CORES[status]} variant="light">
      {ROTULOS_STATUS[status]}
    </Badge>
  );
}
