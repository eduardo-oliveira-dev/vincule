import { Routes, Route, Navigate } from 'react-router-dom';
import { Login } from '../pages/Login';
import { Dashboard } from '../pages/Dashboard';
import { Mural } from '../pages/Mural';
import { Layout } from '../components/Layout';
import { ProtectedRoute } from '../components/ProtectedRoute';

export const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/mural" replace />} />
      <Route path="/login" element={<Login />} />
      
      <Route element={<Layout />}>
        <Route path="/mural" element={<Mural />} />
        
        {/* Protected admin routes */}
        <Route element={<ProtectedRoute requiredRole="ADMIN" />}>
          <Route path="/dashboard" element={<Dashboard />} />
        </Route>
      </Route>
    </Routes>
  );
};
