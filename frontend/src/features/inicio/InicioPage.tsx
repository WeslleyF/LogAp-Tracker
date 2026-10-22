import { Text, Title } from '@mantine/core';
import { useAuth } from '../auth/AuthContext';

export function InicioPage() {
  const { sessao } = useAuth();
  return (
    <>
      <Title order={2}>Olá, {sessao?.nome}</Title>
      <Text c="dimmed">Bem-vindo ao LogiTrack Pro.</Text>
    </>
  );
}
