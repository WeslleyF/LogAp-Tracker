import { Select } from '@mantine/core';
import type { SelectProps } from '@mantine/core';
import { useVeiculos } from './useOpcoes';

interface Props extends Omit<SelectProps, 'data' | 'value' | 'onChange'> {
  value: number | null;
  onChange: (id: number | null) => void;
}

/** Seletor de veículo; o valor do formulário é o id numérico. */
export function CampoVeiculo({ value, onChange, ...resto }: Props) {
  const { data = [], isLoading } = useVeiculos();
  return (
    <Select
      searchable
      placeholder="Selecione o veículo"
      nothingFoundMessage="Nenhum veículo"
      disabled={isLoading}
      data={data.map((v) => ({ value: String(v.id), label: `${v.placa} · ${v.modelo}` }))}
      value={value == null ? null : String(value)}
      onChange={(v) => onChange(v ? Number(v) : null)}
      {...resto}
    />
  );
}
