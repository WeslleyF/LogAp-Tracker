import { notifications } from '@mantine/notifications';
import { keepPreviousData, useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { FieldValues } from 'react-hook-form';
import { extrairProblema } from '../../lib/problema';
import type { CrudConfig } from './types';

export const TAMANHO_PAGINA = 10;

/** '' vira null e textos são aparados, para a API receber campos opcionais como nulos. */
function limparPayload(form: FieldValues) {
  return Object.fromEntries(
    Object.entries(form).map(([chave, valor]) => [
      chave,
      typeof valor === 'string' ? valor.trim() || null : valor,
    ]),
  );
}

function avisarErro(titulo: string, erro: unknown) {
  const problema = extrairProblema(erro);
  // 400 aparece no próprio formulário; o resto (409 de conflito, 404, rede) vira notificação
  if (problema.status === 400) return;
  notifications.show({ title: titulo, message: problema.mensagem, color: 'red' });
}

/** Lista paginada e mutações de uma tela de CRUD, com notificações e atualização da lista. */
export function useCrud<T extends { id: number }, F extends FieldValues>(
  config: CrudConfig<T, F>,
  pagina: number,
) {
  const queryClient = useQueryClient();
  const { api } = config;

  const lista = useQuery({
    queryKey: [api.recurso, 'lista', pagina],
    queryFn: () => api.listar({ page: pagina - 1, size: TAMANHO_PAGINA, sort: config.ordenacao }),
    placeholderData: keepPreviousData,
  });

  // O dashboard e os seletores dependem destes dados.
  const atualizar = () =>
    Promise.all([
      queryClient.invalidateQueries({ queryKey: [api.recurso] }),
      queryClient.invalidateQueries({ queryKey: ['dashboard'] }),
    ]);

  const salvar = useMutation({
    mutationFn: ({ id, form }: { id?: number; form: F }) =>
      id == null ? api.criar(limparPayload(form)) : api.atualizar(id, limparPayload(form)),
    onSuccess: async () => {
      await atualizar();
      notifications.show({ message: `${config.singular} salvo(a) com sucesso`, color: 'green' });
    },
    onError: (erro) => avisarErro('Não foi possível salvar', erro),
  });

  const excluir = useMutation({
    mutationFn: (id: number) => api.excluir(id),
    onSuccess: async () => {
      await atualizar();
      notifications.show({ message: `${config.singular} excluído(a)`, color: 'green' });
    },
    onError: (erro) => avisarErro('Não foi possível excluir', erro),
  });

  return { lista, salvar, excluir };
}
