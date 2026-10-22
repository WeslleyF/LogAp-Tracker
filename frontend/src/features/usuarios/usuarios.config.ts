import { z } from 'zod';
import { createCrudApi } from '../../api/crudApi';
import type { CrudConfig } from '../../components/crud/types';
import { textoObrigatorio } from '../../lib/zod';
import type { Usuario } from '../../types';

const schema = z.object({
  nome: textoObrigatorio('o nome'),
  email: z.string().trim().min(1, 'Informe o e-mail').email('E-mail inválido'),
  // vazia na edição mantém a senha atual; a API exige senha só na criação
  senha: z.string().refine((s) => s === '' || s.length >= 6, 'Mínimo de 6 caracteres'),
  perfil: z.string().min(1, 'Selecione o perfil'),
  ativo: z.boolean(),
});

export type UsuarioForm = z.infer<typeof schema>;

export const usuariosConfig: CrudConfig<Usuario, UsuarioForm> = {
  titulo: 'Usuários',
  singular: 'Usuário',
  api: createCrudApi<Usuario>('usuarios'),
  schema,
  ordenacao: 'nome,asc',
  colunas: [
    { campo: 'nome', titulo: 'Nome' },
    { campo: 'email', titulo: 'E-mail' },
    { campo: 'perfil', titulo: 'Perfil' },
    { campo: 'ativo', titulo: 'Ativo', render: (u) => (u.ativo ? 'Sim' : 'Não') },
  ],
  campos: [
    { nome: 'nome', rotulo: 'Nome', tipo: 'text', obrigatorio: true },
    { nome: 'email', rotulo: 'E-mail', tipo: 'text', obrigatorio: true },
    {
      nome: 'senha',
      rotulo: 'Senha',
      tipo: 'password',
      placeholder: 'Obrigatória ao criar; vazia mantém a atual',
    },
    {
      nome: 'perfil',
      rotulo: 'Perfil',
      tipo: 'select',
      obrigatorio: true,
      opcoes: [
        { value: 'ADMIN', label: 'Administrador' },
        { value: 'OPERADOR', label: 'Operador' },
      ],
    },
    { nome: 'ativo', rotulo: 'Ativo', tipo: 'boolean' },
  ],
  valoresIniciais: { nome: '', email: '', senha: '', perfil: 'OPERADOR', ativo: true },
  paraForm: (u) => ({
    nome: u.nome,
    email: u.email,
    senha: '',
    perfil: u.perfil,
    ativo: u.ativo,
  }),
};
