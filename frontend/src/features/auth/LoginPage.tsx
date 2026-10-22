import { zodResolver } from '@hookform/resolvers/zod';
import {
  Alert,
  Button,
  Center,
  Paper,
  PasswordInput,
  Stack,
  TextInput,
  Title,
} from '@mantine/core';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { z } from 'zod';
import { extrairProblema } from '../../lib/problema';
import { useAuth } from './AuthContext';

const schema = z.object({
  email: z.string().min(1, 'Informe o e-mail').email('E-mail inválido'),
  senha: z.string().min(1, 'Informe a senha'),
});
type LoginForm = z.infer<typeof schema>;

export function LoginPage() {
  const { sessao, entrar } = useAuth();
  const navigate = useNavigate();
  const local = useLocation();
  const [erro, setErro] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginForm>({ resolver: zodResolver(schema) });

  if (sessao) return <Navigate to="/" replace />;

  const destino = (local.state as { de?: string } | null)?.de ?? '/';

  const enviar = handleSubmit(async ({ email, senha }) => {
    setErro(null);
    try {
      await entrar(email, senha);
      navigate(destino, { replace: true });
    } catch (e) {
      const problema = extrairProblema(e);
      setErro(problema.status === 401 ? 'E-mail ou senha inválidos' : problema.mensagem);
    }
  });

  return (
    <Center mih="100vh" bg="gray.0">
      <Paper w={380} p="xl" radius="md" withBorder shadow="sm">
        <form onSubmit={enviar} noValidate>
          <Stack>
            <Title order={2}>LogiTrack Pro</Title>
            {erro && (
              <Alert color="red" variant="light">
                {erro}
              </Alert>
            )}
            <TextInput
              label="E-mail"
              autoComplete="username"
              error={errors.email?.message}
              {...register('email')}
            />
            <PasswordInput
              label="Senha"
              autoComplete="current-password"
              error={errors.senha?.message}
              {...register('senha')}
            />
            <Button type="submit" loading={isSubmitting}>
              Entrar
            </Button>
          </Stack>
        </form>
      </Paper>
    </Center>
  );
}
