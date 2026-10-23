import {
  IconBuildingWarehouse,
  IconChartBar,
  IconRoute,
  IconTool,
  IconUsers,
} from '@tabler/icons-react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { AppShell } from './components/layout/AppShell';
import type { ItemMenu } from './components/layout/AppShell';
import { CrudPage } from './components/crud/CrudPage';
import { LoginPage } from './features/auth/LoginPage';
import { ProtectedRoute } from './features/auth/ProtectedRoute';
import { DashboardPage } from './features/dashboard/DashboardPage';
import { manutencoesConfig } from './features/manutencoes/manutencoes.config';
import { usuariosConfig } from './features/usuarios/usuarios.config';
import { veiculosConfig } from './features/veiculos/veiculos.config';
import { viagensConfig } from './features/viagens/viagens.config';

const menu: ItemMenu[] = [
  { rotulo: 'Dashboard', caminho: '/', icone: <IconChartBar size={18} /> },
  { rotulo: 'Viagens', caminho: '/viagens', icone: <IconRoute size={18} /> },
  { rotulo: 'Veículos', caminho: '/veiculos', icone: <IconBuildingWarehouse size={18} /> },
  { rotulo: 'Manutenções', caminho: '/manutencoes', icone: <IconTool size={18} /> },
  { rotulo: 'Usuários', caminho: '/usuarios', icone: <IconUsers size={18} />, somenteAdmin: true },
];

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<ProtectedRoute />}>
        <Route element={<AppShell menu={menu} />}>
          <Route index element={<DashboardPage />} />
          <Route path="viagens" element={<CrudPage config={viagensConfig} />} />
          <Route path="veiculos" element={<CrudPage config={veiculosConfig} />} />
          <Route path="manutencoes" element={<CrudPage config={manutencoesConfig} />} />
          <Route element={<ProtectedRoute perfis={['ADMIN']} />}>
            <Route path="usuarios" element={<CrudPage config={usuariosConfig} />} />
          </Route>
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
