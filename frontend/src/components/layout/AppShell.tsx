import {
  AppShell as MantineShell,
  Burger,
  Button,
  Group,
  NavLink,
  Text,
  Title,
} from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { IconLogout } from '@tabler/icons-react';
import type { ReactNode } from 'react';
import { NavLink as RouterLink, Outlet } from 'react-router-dom';
import { useAuth } from '../../features/auth/AuthContext';

export interface ItemMenu {
  rotulo: string;
  caminho: string;
  icone: ReactNode;
  somenteAdmin?: boolean;
}

interface Props {
  menu: ItemMenu[];
}

export function AppShell({ menu }: Props) {
  const [aberto, { toggle }] = useDisclosure();
  const { sessao, ehAdmin, sair } = useAuth();

  return (
    <MantineShell
      header={{ height: 56 }}
      navbar={{ width: 230, breakpoint: 'sm', collapsed: { mobile: !aberto } }}
      padding="md"
    >
      <MantineShell.Header>
        <Group h="100%" px="md" justify="space-between">
          <Group>
            <Burger opened={aberto} onClick={toggle} hiddenFrom="sm" size="sm" />
            <Title order={4}>LogiTrack Pro</Title>
          </Group>
          <Group>
            <Text size="sm">
              {sessao?.nome} · {sessao?.perfil}
            </Text>
            <Button
              variant="subtle"
              size="xs"
              leftSection={<IconLogout size={16} />}
              onClick={sair}
            >
              Sair
            </Button>
          </Group>
        </Group>
      </MantineShell.Header>

      <MantineShell.Navbar p="xs">
        {menu
          .filter((item) => !item.somenteAdmin || ehAdmin)
          .map((item) => (
            <NavLink
              key={item.caminho}
              component={RouterLink}
              to={item.caminho}
              end={item.caminho === '/'}
              label={item.rotulo}
              leftSection={item.icone}
              onClick={() => aberto && toggle()}
            />
          ))}
      </MantineShell.Navbar>

      <MantineShell.Main>
        <Outlet />
      </MantineShell.Main>
    </MantineShell>
  );
}
