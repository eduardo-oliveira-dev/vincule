import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore';

interface ProtectedRouteProps {
  requiredRole?: string;
}

export const ProtectedRoute = ({ requiredRole }: ProtectedRouteProps) => {
  const { token, user } = useAuthStore();

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  if (requiredRole && user?.role !== requiredRole) {
    return <Navigate to="/mural" replace />;
  }

  return <Outlet />;
};
