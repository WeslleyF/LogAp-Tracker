import { Autocomplete } from '@mantine/core';
import type { AutocompleteProps } from '@mantine/core';
import { useCidades } from './useOpcoes';

type Props = Omit<AutocompleteProps, 'data'>;

/** Texto livre que sugere as cidades já usadas em outras viagens. */
export function CampoCidade(props: Props) {
  const { data = [] } = useCidades();
  return <Autocomplete data={data} limit={8} {...props} />;
}
