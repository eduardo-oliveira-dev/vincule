import { useQuery } from '@tanstack/react-query';
import { api } from '../services/api';
import { User, Award, Clock, Calendar, Building2 } from 'lucide-react';

interface UserProfile {
  name: string;
  email: string;
  role: string;
  organizationName: string;
  totalDonations: number;
  totalHours: number;
  lastActivity: string | null;
}

const roleLabel = (role: string) => {
  const map: Record<string, string> = {
    ADMIN: 'Administrador',
    VOLUNTEER: 'Voluntário',
    DONOR: 'Doador',
  };
  return map[role] ?? role;
};

export const Profile = () => {
  const { data: profile, isLoading } = useQuery({
    queryKey: ['profile'],
    queryFn: async () => {
      const { data } = await api.get<UserProfile>('/profile');
      return data;
    }
  });

  if (isLoading) {
    return <div className="text-center text-gray-500 py-16">Carregando perfil...</div>;
  }

  if (!profile) {
    return <div className="text-center text-gray-500 py-16">Não foi possível carregar o perfil.</div>;
  }

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      {/* Cabeçalho */}
      <div className="bg-gradient-to-r from-blue-600 to-blue-700 rounded-2xl p-8 text-white shadow-lg">
        <div className="flex items-center gap-5">
          <div className="w-20 h-20 bg-white/20 rounded-full flex items-center justify-center">
            <User size={36} className="text-white" />
          </div>
          <div>
            <h1 className="text-2xl font-bold">{profile.name}</h1>
            <p className="text-blue-200 text-sm">{profile.email}</p>
            <span className="mt-2 inline-block bg-white/20 text-white text-xs font-medium px-3 py-1 rounded-full">
              {roleLabel(profile.role)}
            </span>
          </div>
        </div>
        <div className="mt-5 flex items-center gap-2 text-blue-200 text-sm">
          <Building2 size={15} />
          <span>{profile.organizationName}</span>
        </div>
      </div>

      {/* Cards de impacto */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-white rounded-xl border border-gray-200 shadow-sm p-6 flex flex-col items-center text-center">
          <div className="w-12 h-12 bg-blue-50 rounded-full flex items-center justify-center mb-3">
            <Award size={22} className="text-blue-600" />
          </div>
          <p className="text-3xl font-bold text-gray-900">{profile.totalDonations}</p>
          <p className="text-sm text-gray-500 mt-1">Doações Realizadas</p>
        </div>

        <div className="bg-white rounded-xl border border-gray-200 shadow-sm p-6 flex flex-col items-center text-center">
          <div className="w-12 h-12 bg-purple-50 rounded-full flex items-center justify-center mb-3">
            <Clock size={22} className="text-purple-600" />
          </div>
          <p className="text-3xl font-bold text-gray-900">{profile.totalHours}</p>
          <p className="text-sm text-gray-500 mt-1">Horas Voluntariadas</p>
        </div>

        <div className="bg-white rounded-xl border border-gray-200 shadow-sm p-6 flex flex-col items-center text-center">
          <div className="w-12 h-12 bg-green-50 rounded-full flex items-center justify-center mb-3">
            <Calendar size={22} className="text-green-600" />
          </div>
          <p className="text-sm font-bold text-gray-900 mt-1">
            {profile.lastActivity
              ? new Date(profile.lastActivity).toLocaleDateString('pt-BR')
              : '—'}
          </p>
          <p className="text-sm text-gray-500 mt-1">Última Atividade</p>
        </div>
      </div>

      {/* Mensagem motivacional */}
      <div className="bg-blue-50 border border-blue-100 rounded-xl p-5 text-center">
        <p className="text-blue-700 font-medium">
          {profile.totalHours > 0
            ? `🌟 Incrível! Você já contribuiu com ${profile.totalHours} hora${profile.totalHours > 1 ? 's' : ''} de voluntariado. Continue fazendo a diferença!`
            : '💙 Bem-vindo ao Vincule! Confirme sua primeira presença em um evento para começar a acumular impacto.'}
        </p>
      </div>
    </div>
  );
};
