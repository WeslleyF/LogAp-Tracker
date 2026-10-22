import { Button, Group, Modal, Paper, Stack, Text, Title } from '@mantine/core';
import { IconPlus } from '@tabler/icons-react';
import { useState } from 'react';
import type { FieldValues } from 'react-hook-form';
import { extrairProblema } from '../../lib/problema';
import { DataTable } from './DataTable';
import { EntityForm } from './EntityForm';
import type { CrudConfig } from './types';
import { TAMANHO_PAGINA, useCrud } from './useCrud';

interface Props<T extends { id: number }, F extends FieldValues> {
  config: CrudConfig<T, F>;
}

/** Tela de CRUD completa (tabela, formulário em modal, confirmação de exclusão) a partir de uma config. */
export function CrudPage<T extends { id: number }, F extends FieldValues>({ config }: Props<T, F>) {
  const [pagina, setPagina] = useState(1);
  // `undefined` = modal fechado; `null` = novo registro; objeto = edição
  const [editando, setEditando] = useState<T | null | undefined>(undefined);
  const [excluindo, setExcluindo] = useState<T | null>(null);
  const { lista, salvar, excluir } = useCrud(config, pagina);

  const problema = salvar.error ? extrairProblema(salvar.error) : null;

  const fecharFormulario = () => {
    setEditando(undefined);
    salvar.reset();
  };

  const enviar = (form: F) =>
    salvar.mutate({ id: editando?.id, form }, { onSuccess: fecharFormulario });

  const confirmarExclusao = () => {
    if (!excluindo) return;
    excluir.mutate(excluindo.id, {
      onSuccess: () => {
        // Excluiu o último da página: volta uma
        if (lista.data?.content.length === 1 && pagina > 1) setPagina(pagina - 1);
      },
      onSettled: () => setExcluindo(null),
    });
  };

  return (
    <Stack>
      <Group justify="space-between">
        <Title order={2}>{config.titulo}</Title>
        <Button leftSection={<IconPlus size={16} />} onClick={() => setEditando(null)}>
          Novo
        </Button>
      </Group>

      <Paper withBorder p="md" radius="md">
        <DataTable
          colunas={config.colunas}
          dados={lista.data?.content ?? []}
          carregando={lista.isLoading}
          pagina={pagina}
          total={lista.data?.totalElements ?? 0}
          tamanho={TAMANHO_PAGINA}
          onPagina={setPagina}
          onEditar={setEditando}
          onExcluir={setExcluindo}
        />
      </Paper>

      <Modal
        opened={editando !== undefined}
        onClose={fecharFormulario}
        title={`${editando ? 'Editar' : 'Novo(a)'} ${config.singular.toLowerCase()}`}
        centered
      >
        {editando !== undefined && (
          <EntityForm
            key={editando?.id ?? 'novo'}
            campos={config.campos}
            schema={config.schema}
            valoresIniciais={editando ? config.paraForm(editando) : config.valoresIniciais}
            enviando={salvar.isPending}
            problema={problema}
            onSubmit={enviar}
            onCancelar={fecharFormulario}
          />
        )}
      </Modal>

      <Modal
        opened={excluindo !== null}
        onClose={() => setExcluindo(null)}
        title={`Excluir ${config.singular.toLowerCase()}?`}
        centered
      >
        <Stack>
          <Text>Esta ação não pode ser desfeita.</Text>
          {config.avisoExclusao && (
            <Text c="red" fw={500}>
              {config.avisoExclusao}
            </Text>
          )}
          <Group justify="flex-end">
            <Button variant="default" onClick={() => setExcluindo(null)}>
              Cancelar
            </Button>
            <Button color="red" loading={excluir.isPending} onClick={confirmarExclusao}>
              Excluir
            </Button>
          </Group>
        </Stack>
      </Modal>
    </Stack>
  );
}
