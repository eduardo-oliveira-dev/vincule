import { Outlet, Link, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore';
import { LogOut, Home, LayoutDashboard } from 'lucide-react';

export const Layout = () => {
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen flex flex-col">
      <header className="bg-blue-600 text-white shadow-md">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16 items-center">
            <div className="flex space-x-4 items-center">
              <span className="font-bold text-xl tracking-wider">Vincule</span>
              <nav className="hidden md:flex space-x-4">
                <Link to="/mural" className="hover:bg-blue-700 px-3 py-2 rounded-md flex items-center space-x-1">
                  <Home size={18} />
                  <span>Mural</span>
                </Link>
                {user?.role === 'ADMIN' && (
                  <Link to="/dashboard" className="hover:bg-blue-700 px-3 py-2 rounded-md flex items-center space-x-1">
                    <LayoutDashboard size={18} />
                    <span>Dashboard</span>
                  </Link>
                )}
              </nav>
            </div>
            
            <div className="flex items-center space-x-4">
              {user ? (
                <>
                  <span className="text-sm hidden sm:block">Olá, {user.name}</span>
                  <button 
                    onClick={handleLogout}
                    className="p-2 bg-blue-700 hover:bg-blue-800 rounded-full transition-colors"
                    title="Sair"
                  >
                    <LogOut size={18} />
                  </button>
                </>
              ) : (
                <Link to="/login" className="bg-white text-blue-600 px-4 py-2 rounded-md font-medium hover:bg-gray-100 transition-colors">
                  Entrar
                </Link>
              )}
            </div>
          </div>
        </div>
      </header>
      
      <main className="flex-1 max-w-7xl w-full mx-auto p-4 sm:p-6 lg:p-8">
        <Outlet />
      </main>
    </div>
  );
};
