import { createElement } from 'react';
import { z } from 'zod';
import { createCrudApi } from '../../api/crudApi';
import { ROTULOS_STATUS, StatusManutencaoBadge } from '../../components/StatusManutencaoBadge';
import type { CrudConfig } from '../../components/crud/types';
import { dataObrigatoria, numeroObrigatorio, textoObrigatorio } from '../../lib/zod';
import type { Manutencao } from '../../types';

const SERVICOS_SUGERIDOS = ['Troca de Óleo', 'Revisão de Freios', 'Troca de Pneus', 'Motor'];

const schema = z
  .object({
    veiculoId: numeroObrigatorio('Selecione o veículo'),
    dataInicio: dataObrigatoria('Informe o início'),
    dataFinalizacao: z.string(),
    tipoServico: textoObrigatorio('o serviço'),
    custoEstimado: numeroObrigatorio('Informe o custo').min(0, 'Custo não pode ser negativo'),
    status: z.string().min(1, 'Selecione o status'),
  })
  .refine((v) => !v.dataFinalizacao || v.dataFinalizacao >= v.dataInicio, {
    path: ['dataFinalizacao'],
    message: 'Finalização não pode ser anterior ao início',
  });

export type ManutencaoForm = z.infer<typeof schema>;

export const manutencoesConfig: CrudConfig<Manutencao, ManutencaoForm> = {
  titulo: 'Manutenções',
  singular: 'Manutenção',
  api: createCrudApi<Manutencao>('manutencoes'),
  schema,
  ordenacao: 'dataInicio,desc',
  colunas: [
    { campo: 'veiculo', titulo: 'Veículo', render: (m) => m.veiculo.placa },
    { campo: 'tipoServico', titulo: 'Serviço' },
    { campo: 'dataInicio', titulo: 'Início', formato: 'data' },
    { campo: 'dataFinalizacao', titulo: 'Finalização', formato: 'data' },
    { campo: 'custoEstimado', titulo: 'Custo estimado', formato: 'moeda' },
    {
      campo: 'status',
      titulo: 'Status',
      render: (m) => createElement(StatusManutencaoBadge, { status: m.status }),
    },
  ],
  campos: [
    { nome: 'veiculoId', rotulo: 'Veículo', tipo: 'veiculo', obrigatorio: true },
    {
      nome: 'tipoServico',
      rotulo: 'Serviço',
      tipo: 'text',
      obrigatorio: true,
      sugestoes: SERVICOS_SUGERIDOS,
    },
    { nome: 'dataInicio', rotulo: 'Início', tipo: 'date', obrigatorio: true },
    { nome: 'dataFinalizacao', rotulo: 'Finalização', tipo: 'date' },
    {
      nome: 'custoEstimado',
      rotulo: 'Custo estimado (R$)',
      tipo: 'number',
      decimais: 2,
      obrigatorio: true,
    },
    {
      nome: 'status',
      rotulo: 'Status',
      tipo: 'select',
      obrigatorio: true,
      opcoes: Object.entries(ROTULOS_STATUS).map(([value, label]) => ({ value, label })),
    },
  ],
  valoresIniciais: {
    dataInicio: '',
    dataFinalizacao: '',
    tipoServico: '',
    status: 'PENDENTE',
  },
  paraForm: (m) => ({
    veiculoId: m.veiculo.id,
    dataInicio: m.dataInicio,
    dataFinalizacao: m.dataFinalizacao ?? '',
    tipoServico: m.tipoServico,
    custoEstimado: m.custoEstimado,
    status: m.status,
  }),
};
