import { Paper, Text } from '@mantine/core';
import type { ReactNode } from 'react';

interface Props {
  titulo: string;
  /** Controles do card (seletor de data etc.). */
  acao?: ReactNode;
  children: ReactNode;
}

/** Moldura de um card do dashboard que contém gráfico ou lista. */
export function ChartCard({ titulo, acao, children }: Props) {
  return (
    <Paper withBorder p="md" radius="md" h="100%">
      <Text size="sm" c="dimmed" fw={500} mb="xs">
        {titulo}
      </Text>
      {acao}
      {children}
    </Paper>
  );
}
