import { z } from 'zod';
import { createCrudApi } from '../../api/crudApi';
import type { CrudConfig } from '../../components/crud/types';
import { numeroObrigatorio, textoObrigatorio } from '../../lib/zod';
import type { Veiculo } from '../../types';

const anoMaximo = new Date().getFullYear() + 1;

const schema = z.object({
  placa: z
    .string()
    .trim()
    .regex(/^([A-Za-z]{3}-\d{4}|[A-Za-z]{3}\d[A-Za-z]\d{2})$/, 'Use ABC-1234 ou Mercosul ABC1D23'),
  modelo: textoObrigatorio('o modelo', 50),
  tipo: z.string().min(1, 'Selecione o tipo'),
  ano: numeroObrigatorio('Informe o ano')
    .min(1980, `Ano entre 1980 e ${anoMaximo}`)
    .max(anoMaximo, `Ano entre 1980 e ${anoMaximo}`),
});

export type VeiculoForm = z.infer<typeof schema>;

export const veiculosConfig: CrudConfig<Veiculo, VeiculoForm> = {
  titulo: 'Veículos',
  singular: 'Veículo',
  api: createCrudApi<Veiculo>('veiculos'),
  schema,
  ordenacao: 'placa,asc',
  avisoExclusao: 'Excluir um veículo também apaga todas as viagens e manutenções dele.',
  colunas: [
    { campo: 'placa', titulo: 'Placa' },
    { campo: 'modelo', titulo: 'Modelo' },
    { campo: 'tipo', titulo: 'Tipo' },
    { campo: 'ano', titulo: 'Ano' },
  ],
  campos: [
    { nome: 'placa', rotulo: 'Placa', tipo: 'text', obrigatorio: true, placeholder: 'ABC-1234' },
    { nome: 'modelo', rotulo: 'Modelo', tipo: 'text', obrigatorio: true },
    {
      nome: 'tipo',
      rotulo: 'Tipo',
      tipo: 'select',
      obrigatorio: true,
      opcoes: [
        { value: 'LEVE', label: 'Leve' },
        { value: 'PESADO', label: 'Pesado' },
      ],
    },
    { nome: 'ano', rotulo: 'Ano', tipo: 'number', obrigatorio: true },
  ],
  valoresIniciais: { placa: '', modelo: '', tipo: '' },
  paraForm: (v) => ({ placa: v.placa, modelo: v.modelo, tipo: v.tipo, ano: v.ano }),
};
