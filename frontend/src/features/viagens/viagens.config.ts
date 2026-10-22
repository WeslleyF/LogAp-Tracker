import dayjs from 'dayjs';
import { createElement } from 'react';
import { z } from 'zod';
import { createCrudApi } from '../../api/crudApi';
import type { CrudConfig } from '../../components/crud/types';
import {
  dataObrigatoria,
  numeroObrigatorio,
  numeroPositivoOpcional,
  textoObrigatorio,
} from '../../lib/zod';
import type { Viagem } from '../../types';
import { ChegadaViagem } from './ChegadaViagem';

const schema = z
  .object({
    veiculoId: numeroObrigatorio('Selecione o veículo'),
    dataSaida: dataObrigatoria('Informe a saída'),
    dataChegada: z.string(),
    origem: textoObrigatorio('a origem'),
    destino: textoObrigatorio('o destino'),
    kmPercorrida: numeroPositivoOpcional('KM deve ser maior que zero'),
  })
  .refine((v) => !v.dataChegada || v.dataChegada > v.dataSaida, {
    path: ['dataChegada'],
    message: 'Chegada deve ser posterior à saída',
  })
  .refine((v) => v.origem.trim().toLowerCase() !== v.destino.trim().toLowerCase(), {
    path: ['destino'],
    message: 'Destino deve ser diferente da origem',
  });

export type ViagemForm = z.infer<typeof schema>;

const paraIso = (valor: string | null) => (valor ? dayjs(valor).format('YYYY-MM-DDTHH:mm:ss') : '');

export const viagensConfig: CrudConfig<Viagem, ViagemForm> = {
  titulo: 'Viagens',
  singular: 'Viagem',
  api: createCrudApi<Viagem>('viagens'),
  schema,
  ordenacao: 'dataSaida,desc',
  colunas: [
    { campo: 'veiculo', titulo: 'Veículo', render: (v) => v.veiculo.placa },
    { campo: 'dataSaida', titulo: 'Saída', formato: 'datahora' },
    {
      campo: 'chegadaEfetiva',
      titulo: 'Chegada',
      render: (v) => createElement(ChegadaViagem, { viagem: v }),
    },
    { campo: 'origem', titulo: 'Origem' },
    { campo: 'destino', titulo: 'Destino' },
    { campo: 'kmPercorrida', titulo: 'KM', formato: 'km' },
  ],
  campos: [
    { nome: 'veiculoId', rotulo: 'Veículo', tipo: 'veiculo', obrigatorio: true },
    { nome: 'dataSaida', rotulo: 'Saída', tipo: 'datetime', obrigatorio: true },
    { nome: 'dataChegada', rotulo: 'Chegada', tipo: 'datetime' },
    { nome: 'origem', rotulo: 'Origem', tipo: 'cidade', obrigatorio: true },
    { nome: 'destino', rotulo: 'Destino', tipo: 'cidade', obrigatorio: true },
    { nome: 'kmPercorrida', rotulo: 'KM percorrida', tipo: 'number', decimais: 2 },
  ],
  valoresIniciais: { dataSaida: '', dataChegada: '', origem: '', destino: '', kmPercorrida: '' },
  paraForm: (v) => ({
    veiculoId: v.veiculo.id,
    dataSaida: paraIso(v.dataSaida),
    dataChegada: paraIso(v.dataChegada),
    origem: v.origem,
    destino: v.destino,
    kmPercorrida: v.kmPercorrida ?? '',
  }),
};
