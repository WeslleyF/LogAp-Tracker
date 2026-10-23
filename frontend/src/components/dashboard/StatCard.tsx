import { Paper, Text } from '@mantine/core';
import type { ReactNode } from 'react';

interface Props {
  titulo: string;
  valor: string;
  /** Seletor ou legenda sob o número. */
  children?: ReactNode;
}

/** Um número de destaque com título. */
export function StatCard({ titulo, valor, children }: Props) {
  return (
    <Paper withBorder p="md" radius="md" h="100%">
      <Text size="sm" c="dimmed" fw={500}>
        {titulo}
      </Text>
      <Text fz={32} fw={700} lh={1.2} my="xs">
        {valor}
      </Text>
      {children}
    </Paper>
  );
}
