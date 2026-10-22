import { Navigate, Outlet, useLocation } from 'react-router-dom';
import type { Perfil } from '../../types';
import { useAuth } from './AuthContext';

interface Props {
  /** Se informado, só esses perfis acessam; os demais voltam para o início. */
  perfis?: Perfil[];
}

export function ProtectedRoute({ perfis }: Props) {
  const { sessao } = useAuth();
  const local = useLocation();

  if (!sessao) return <Navigate to="/login" replace state={{ de: local.pathname }} />;
  if (perfis && !perfis.includes(sessao.perfil)) return <Navigate to="/" replace />;
  return <Outlet />;
}
