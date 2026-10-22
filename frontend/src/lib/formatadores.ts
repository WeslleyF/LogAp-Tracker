import dayjs from 'dayjs';

const moeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
const numero = new Intl.NumberFormat('pt-BR', { maximumFractionDigits: 2 });

export const formatarData = (iso: string | null | undefined) =>
  iso ? dayjs(iso).format('DD/MM/YYYY') : '—';

export const formatarDataHora = (iso: string | null | undefined) =>
  iso ? dayjs(iso).format('DD/MM/YYYY HH:mm') : '—';

export const formatarKm = (km: number | null | undefined) =>
  km == null ? '—' : `${numero.format(km)} km`;

export const formatarMoeda = (valor: number | null | undefined) =>
  valor == null ? '—' : moeda.format(valor);

export const formatarNumero = (valor: number) => numero.format(valor);
