import { Autocomplete, NumberInput, PasswordInput, Select, Switch, TextInput } from '@mantine/core';
import { DateInput, DateTimePicker } from '@mantine/dates';
import dayjs from 'dayjs';
import { Controller } from 'react-hook-form';
import type { Control, FieldValues, Path } from 'react-hook-form';
import { CampoCidade } from './CampoCidade';
import { CampoVeiculo } from './CampoVeiculo';
import type { CrudCampo } from './types';

interface Props<F extends FieldValues> {
  campo: CrudCampo<F>;
  control: Control<F>;
}

const FORMATO_DATA = 'YYYY-MM-DD';
const FORMATO_DATA_HORA = 'YYYY-MM-DDTHH:mm:ss';

const paraDate = (valor: unknown) => (valor ? dayjs(valor as string).toDate() : null);

/** Um input do formulário conforme o `tipo` do campo; datas trafegam como string ISO. */
export function FormField<F extends FieldValues>({ campo, control }: Props<F>) {
  return (
    <Controller
      control={control}
      name={campo.nome as Path<F>}
      render={({ field, fieldState }) => {
        const base = {
          label: campo.rotulo,
          error: fieldState.error?.message,
          withAsterisk: campo.obrigatorio,
          placeholder: campo.placeholder,
        };
        const texto = (field.value ?? '') as string;

        switch (campo.tipo) {
          case 'password':
            return <PasswordInput {...base} value={texto} onChange={field.onChange} />;
          case 'number':
            return (
              <NumberInput
                {...base}
                value={(field.value ?? '') as number | string}
                onChange={field.onChange}
                decimalScale={campo.decimais ?? 0}
                decimalSeparator=","
                thousandSeparator="."
                allowNegative={false}
                hideControls
              />
            );
          case 'date':
            return (
              <DateInput
                {...base}
                value={paraDate(field.value)}
                onChange={(d) => field.onChange(d ? dayjs(d).format(FORMATO_DATA) : '')}
                valueFormat="DD/MM/YYYY"
                clearable
              />
            );
          case 'datetime':
            return (
              <DateTimePicker
                {...base}
                value={paraDate(field.value)}
                onChange={(d) => field.onChange(d ? dayjs(d).format(FORMATO_DATA_HORA) : '')}
                valueFormat="DD/MM/YYYY HH:mm"
                clearable
              />
            );
          case 'select':
            return (
              <Select
                {...base}
                data={campo.opcoes ?? []}
                value={texto || null}
                onChange={(v) => field.onChange(v ?? '')}
                allowDeselect={false}
              />
            );
          case 'veiculo':
            return (
              <CampoVeiculo
                {...base}
                value={(field.value ?? null) as number | null}
                onChange={field.onChange}
              />
            );
          case 'cidade':
            return <CampoCidade {...base} value={texto} onChange={field.onChange} />;
          case 'boolean':
            return (
              <Switch
                label={campo.rotulo}
                error={fieldState.error?.message}
                checked={Boolean(field.value)}
                onChange={(e) => field.onChange(e.currentTarget.checked)}
              />
            );
          default:
            return campo.sugestoes ? (
              <Autocomplete
                {...base}
                data={campo.sugestoes}
                value={texto}
                onChange={field.onChange}
              />
            ) : (
              <TextInput {...base} value={texto} onChange={field.onChange} />
            );
        }
      }}
    />
  );
}
