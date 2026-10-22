// Tipos espelhando os records do back (JSON camelCase, datas ISO sem fuso).

export type Perfil = 'ADMIN' | 'OPERADOR';
export type TipoVeiculo = 'LEVE' | 'PESADO';
export type StatusManutencao = 'PENDENTE' | 'EM_REALIZACAO' | 'CONCLUIDA' | 'REJEITADA';

export interface Page<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
}

export interface Sessao {
  token: string;
  expiraEm: string;
  id: number;
  nome: string;
  perfil: Perfil;
}

export interface VeiculoResumo {
  id: number;
  placa: string;
  modelo: string;
}

export interface Veiculo extends VeiculoResumo {
  tipo: TipoVeiculo;
  ano: number;
}

export interface Viagem {
  id: number;
  veiculo: VeiculoResumo;
  dataSaida: string;
  dataChegada: string | null;
  chegadaEfetiva: string | null;
  emAndamento: boolean;
  origem: string;
  destino: string;
  kmPercorrida: number | null;
}

export interface Manutencao {
  id: number;
  veiculo: VeiculoResumo;
  dataInicio: string;
  dataFinalizacao: string | null;
  tipoServico: string;
  custoEstimado: number;
  status: StatusManutencao;
}

export interface Usuario {
  id: number;
  nome: string;
  email: string;
  perfil: Perfil;
  ativo: boolean;
}

export interface TotalKm {
  totalKm: number;
}

export interface VolumeCategoria {
  tipo: TipoVeiculo;
  totalViagens: number;
}

export interface RankingVeiculo {
  posicao: number;
  veiculoId: number;
  placa: string;
  modelo: string;
  tipo: TipoVeiculo;
  totalKm: number;
}

export interface ManutencaoAgendada {
  id: number;
  veiculoId: number;
  placa: string;
  modelo: string;
  tipoServico: string;
  dataInicio: string;
  dataFinalizacao: string | null;
  custoEstimado: number;
  status: StatusManutencao;
}

export interface ProjecaoFinanceira {
  mesReferencia: string;
  total: number;
  quantidade: number;
  porStatus: { status: StatusManutencao; quantidade: number; total: number }[];
}

export interface ResumoDashboard {
  totalKm: TotalKm;
  volumeCategoria: VolumeCategoria[];
  rankingUtilizacao: RankingVeiculo[];
  cronograma: ManutencaoAgendada[];
  projecaoFinanceira: ProjecaoFinanceira;
}
