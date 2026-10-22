import { ActionIcon, Center, Group, Loader, Pagination, Table, Text } from '@mantine/core';
import { IconPencil, IconTrash } from '@tabler/icons-react';
import type { ReactNode } from 'react';
import { formatarData, formatarDataHora, formatarKm, formatarMoeda } from '../../lib/formatadores';
import type { CrudColuna, Formato } from './types';

interface Props<T extends { id: number }> {
  colunas: CrudColuna<T>[];
  dados: T[];
  carregando: boolean;
  /** Página atual, base 1. */
  pagina: number;
  total: number;
  tamanho: number;
  onPagina: (pagina: number) => void;
  onEditar: (item: T) => void;
  onExcluir: (item: T) => void;
}

const FORMATADORES: Record<Formato, (valor: never) => string> = {
  data: formatarData,
  datahora: formatarDataHora,
  km: formatarKm,
  moeda: formatarMoeda,
};

function celula<T>(item: T, coluna: CrudColuna<T>): ReactNode {
  if (coluna.render) return coluna.render(item);
  const valor = item[coluna.campo];
  if (coluna.formato) return FORMATADORES[coluna.formato](valor as never);
  return valor == null ? '—' : String(valor);
}

export function DataTable<T extends { id: number }>({
  colunas,
  dados,
  carregando,
  pagina,
  total,
  tamanho,
  onPagina,
  onEditar,
  onExcluir,
}: Props<T>) {
  if (carregando && dados.length === 0) {
    return (
      <Center py="xl">
        <Loader />
      </Center>
    );
  }

  const paginas = Math.max(1, Math.ceil(total / tamanho));

  return (
    <>
      <Table.ScrollContainer minWidth={640}>
        <Table striped highlightOnHover verticalSpacing="xs">
          <Table.Thead>
            <Table.Tr>
              {colunas.map((c) => (
                <Table.Th key={c.campo}>{c.titulo}</Table.Th>
              ))}
              <Table.Th w={90} />
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {dados.map((item) => (
              <Table.Tr key={item.id}>
                {colunas.map((c) => (
                  <Table.Td key={c.campo}>{celula(item, c)}</Table.Td>
                ))}
                <Table.Td>
                  <Group gap={4} wrap="nowrap">
                    <ActionIcon variant="subtle" aria-label="Editar" onClick={() => onEditar(item)}>
                      <IconPencil size={16} />
                    </ActionIcon>
                    <ActionIcon
                      variant="subtle"
                      color="red"
                      aria-label="Excluir"
                      onClick={() => onExcluir(item)}
                    >
                      <IconTrash size={16} />
                    </ActionIcon>
                  </Group>
                </Table.Td>
              </Table.Tr>
            ))}
          </Table.Tbody>
        </Table>
      </Table.ScrollContainer>

      {dados.length === 0 && (
        <Text c="dimmed" ta="center" py="md">
          Nenhum registro
        </Text>
      )}

      {paginas > 1 && (
        <Group justify="flex-end" mt="md">
          <Pagination value={pagina} onChange={onPagina} total={paginas} />
        </Group>
      )}
    </>
  );
}
