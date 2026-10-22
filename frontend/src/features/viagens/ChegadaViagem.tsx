import { Badge, Text } from '@mantine/core';
import { formatarDataHora } from '../../lib/formatadores';
import type { Viagem } from '../../types';

/** Chegada informada, chegada inferida pela próxima saída, ou "Em andamento". */
export function ChegadaViagem({ viagem }: { viagem: Viagem }) {
  if (viagem.emAndamento) {
    return (
      <Badge color="blue" variant="light">
        Em andamento
      </Badge>
    );
  }
  return (
    <>
      {formatarDataHora(viagem.chegadaEfetiva)}
      {viagem.dataChegada == null && (
        <Text span size="xs" c="dimmed">
          {' '}
          (próxima saída)
        </Text>
      )}
    </>
  );
}
