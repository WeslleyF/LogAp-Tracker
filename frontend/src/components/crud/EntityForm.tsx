import { zodResolver } from '@hookform/resolvers/zod';
import { Alert, Button, Group, Stack } from '@mantine/core';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import type { DefaultValues, FieldValues, Path } from 'react-hook-form';
import type { ZodType, ZodTypeDef } from 'zod';
import type { Problema } from '../../lib/problema';
import { FormField } from './FormField';
import type { CrudCampo } from './types';

interface Props<F extends FieldValues> {
  campos: CrudCampo<F>[];
  schema: ZodType<F, ZodTypeDef, F>;
  valoresIniciais: DefaultValues<F> | F;
  enviando: boolean;
  /** Erro 400 da API: os erros por campo aparecem no próprio input. */
  problema: Problema | null;
  onSubmit: (valores: F) => void;
  onCancelar: () => void;
}

/**
 * Regras entre campos chegam da API com a chave `<campo>Valida` ou `<campo>Diferente...`; a chave
 * é associada ao campo cujo nome é o maior prefixo dela.
 */
function campoDoErro<F extends FieldValues>(chave: string, campos: CrudCampo<F>[]) {
  return campos
    .filter((c) => chave.startsWith(c.nome))
    .sort((a, b) => b.nome.length - a.nome.length)[0];
}

export function EntityForm<F extends FieldValues>({
  campos,
  schema,
  valoresIniciais,
  enviando,
  problema,
  onSubmit,
  onCancelar,
}: Props<F>) {
  const { control, handleSubmit, setError } = useForm<F>({
    resolver: zodResolver(schema),
    defaultValues: valoresIniciais as DefaultValues<F>,
  });
  const [avisos, setAvisos] = useState<string[]>([]);

  useEffect(() => {
    const extras: string[] = [];
    Object.entries(problema?.erros ?? {}).forEach(([chave, mensagem]) => {
      const campo = campoDoErro(chave, campos);
      if (campo) setError(campo.nome as Path<F>, { type: 'server', message: mensagem });
      else extras.push(mensagem);
    });
    setAvisos(extras);
  }, [problema, campos, setError]);

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate>
      <Stack>
        {avisos.map((aviso) => (
          <Alert key={aviso} color="red" variant="light">
            {aviso}
          </Alert>
        ))}
        {campos.map((campo) => (
          <FormField key={campo.nome} campo={campo} control={control} />
        ))}
        <Group justify="flex-end">
          <Button variant="default" onClick={onCancelar} disabled={enviando}>
            Cancelar
          </Button>
          <Button type="submit" loading={enviando}>
            Salvar
          </Button>
        </Group>
      </Stack>
    </form>
  );
}
