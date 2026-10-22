import { IconHome } from '@tabler/icons-react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { AppShell } from './components/layout/AppShell';
import type { ItemMenu } from './components/layout/AppShell';
import { LoginPage } from './features/auth/LoginPage';
import { ProtectedRoute } from './features/auth/ProtectedRoute';
import { InicioPage } from './features/inicio/InicioPage';

const menu: ItemMenu[] = [{ rotulo: 'Início', caminho: '/', icone: <IconHome size={18} /> }];

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<ProtectedRoute />}>
        <Route element={<AppShell menu={menu} />}>
          <Route index element={<InicioPage />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
