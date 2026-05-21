import { Routes, Route, Navigate } from 'react-router-dom';
import { Login } from '../pages/Login';
import { Dashboard } from '../pages/Dashboard';
import { Mural } from '../pages/Mural';
import { Inventory } from '../pages/Inventory';
import { Events } from '../pages/Events';
import { Profile } from '../pages/Profile';
import { Layout } from '../components/Layout';
import { ProtectedRoute } from '../components/ProtectedRoute';

export const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/mural" replace />} />
      <Route path="/login" element={<Login />} />

      <Route element={<Layout />}>
        <Route path="/mural" element={<Mural />} />

        {/* Rotas protegidas — qualquer usuário autenticado */}
        <Route element={<ProtectedRoute />}>
          <Route path="/profile" element={<Profile />} />
        </Route>

        {/* Rotas protegidas — apenas ADMIN */}
        <Route element={<ProtectedRoute requiredRole="ADMIN" />}>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/inventory" element={<Inventory />} />
          <Route path="/events" element={<Events />} />
        </Route>
      </Route>
    </Routes>
  );
};
