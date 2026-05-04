import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../services/api';
import { AlertTriangle, CalendarDays, Users, CheckCircle } from 'lucide-react';
import { useAuthStore } from '../store/useAuthStore';
import { useNavigate } from 'react-router-dom';

interface CriticalItem {
  id: string;
  name: string;
  quantity: number;
  minimumStock: number;
  unit: string;
  category: string;
}

interface VolunteerEvent {
  id: string;
  title: string;
  description: string;
  eventDate: string;
  maxVolunteers: number;
  confirmedSlots: number;
  category: string;
}

export const Mural = () => {
  const queryClient = useQueryClient();
  const { isAuthenticated } = useAuthStore();
  const navigate = useNavigate();
  const { data: criticalItems, isLoading: loadingItems } = useQuery({
    queryKey: ['criticalItems'],
    queryFn: async () => {
      const { data } = await api.get<CriticalItem[]>('/public/inventory/critical');
      return data;
    }
  });

  const { data: events, isLoading: loadingEvents } = useQuery({
    queryKey: ['publicEvents'],
    queryFn: async () => {
      const { data } = await api.get<VolunteerEvent[]>('/public/events');
      return data;
    }
  });

  const confirmMutation = useMutation({
    mutationFn: async (eventId: string) => {
      await api.post(`/events/${eventId}/confirm`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['publicEvents'] });
      alert("Presença confirmada com sucesso! Muito obrigado pelo seu apoio.");
    },
    onError: (error: any) => {
      const msg = error.response?.data?.message || "Erro ao tentar confirmar presença. Tente novamente.";
      alert(msg);
    }
  });

  const handleParticipate = (eventId: string) => {
    if (!isAuthenticated()) {
      alert("Você precisa fazer login ou se cadastrar para participar de eventos!");
      navigate('/login');
      return;
    }
    confirmMutation.mutate(eventId);
  };

  return (
    <div className="space-y-10">
      <div className="bg-white p-8 rounded-xl shadow-sm border border-gray-100 text-center">
        <h1 className="text-3xl font-extrabold text-gray-900 mb-4 tracking-tight">Mural de Ações e Impacto</h1>
        <p className="text-gray-600 max-w-2xl mx-auto text-lg leading-relaxed">
          Acompanhe as últimas ações de voluntariado e as necessidades urgentes da comunidade Vincule.
          Juntos, construímos um impacto social real e duradouro.
        </p>
      </div>
      
      {/* Seção de Alertas Críticos */}
      <div>
        <div className="flex items-center space-x-2 mb-6">
          <AlertTriangle className="text-red-500" size={24} />
          <h2 className="text-2xl font-bold text-gray-800">Necessidades Urgentes</h2>
        </div>
        
        {loadingItems ? (
          <div className="animate-pulse flex space-x-4">
            <div className="flex-1 space-y-4 py-1">
              <div className="h-4 bg-gray-200 rounded w-3/4"></div>
              <div className="space-y-2">
                <div className="h-4 bg-gray-200 rounded"></div>
                <div className="h-4 bg-gray-200 rounded w-5/6"></div>
              </div>
            </div>
          </div>
        ) : criticalItems && criticalItems.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {criticalItems.map((item) => (
              <div key={item.id} className="bg-red-50 border border-red-100 rounded-xl overflow-hidden shadow-sm hover:shadow-md transition-all">
                <div className="p-5">
                  <div className="flex justify-between items-start">
                    <span className="text-xs font-bold text-red-600 uppercase tracking-wider bg-red-100 px-2 py-1 rounded-full">{item.category}</span>
                    <span className="text-red-500 font-bold text-lg">{item.quantity} {item.unit}</span>
                  </div>
                  <h3 className="mt-4 text-xl font-bold text-gray-900">{item.name}</h3>
                  <p className="mt-2 text-sm text-red-700 font-medium">
                    Estoque mínimo necessário: {item.minimumStock} {item.unit}
                  </p>
                  <button 
                    onClick={() => alert(`Obrigado pelo interesse em doar "${item.name}"! Entre em contato com a organização responsável para combinar a entrega. Quantidade necessária: ${item.minimumStock - item.quantity} ${item.unit}.`)}
                    className="mt-5 w-full bg-red-600 text-white font-medium py-2.5 px-4 rounded-lg hover:bg-red-700 transition-colors shadow-sm"
                  >
                    Quero Doar
                  </button>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="bg-gray-50 border border-gray-200 rounded-xl p-8 text-center text-gray-500">
            Nenhuma necessidade urgente no momento. Graças à comunidade!
          </div>
        )}
      </div>

      {/* Seção de Eventos */}
      <div>
        <div className="flex items-center space-x-2 mb-6">
          <CalendarDays className="text-blue-600" size={24} />
          <h2 className="text-2xl font-bold text-gray-800">Próximos Eventos</h2>
        </div>

        {loadingEvents ? (
          <div className="animate-pulse flex space-x-4">
            <div className="flex-1 space-y-4 py-1">
              <div className="h-4 bg-gray-200 rounded w-3/4"></div>
            </div>
          </div>
        ) : events && events.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {events.map((event) => (
              <div key={event.id} className="bg-white border border-gray-200 rounded-xl overflow-hidden shadow-sm hover:shadow-md transition-all flex flex-col">
                <div className="p-6 flex-1">
                  <span className="text-xs font-bold text-blue-600 uppercase tracking-wider bg-blue-50 px-2 py-1 rounded-full">{event.category}</span>
                  <h3 className="mt-4 text-xl font-bold text-gray-900 leading-tight">{event.title}</h3>
                  <p className="mt-2 text-sm text-gray-600 line-clamp-2">{event.description}</p>
                  
                  <div className="mt-4 flex items-center text-sm text-gray-500 space-x-4">
                    <div className="flex items-center space-x-1">
                      <CalendarDays size={16} className="text-gray-400" />
                      <span>{new Date(event.eventDate).toLocaleDateString('pt-BR')}</span>
                    </div>
                    <div className="flex items-center space-x-1">
                      <Users size={16} className="text-gray-400" />
                      <span>{event.confirmedSlots}/{event.maxVolunteers} vagas</span>
                    </div>
                  </div>
                </div>
                <div className="p-4 border-t border-gray-100 bg-gray-50">
                  <button 
                    onClick={() => handleParticipate(event.id)}
                    disabled={confirmMutation.isPending || event.confirmedSlots >= event.maxVolunteers}
                    className={`w-full font-medium py-2.5 px-4 border rounded-lg shadow-sm flex justify-center items-center gap-2 transition-colors
                      ${event.confirmedSlots >= event.maxVolunteers 
                        ? 'bg-gray-100 text-gray-500 border-gray-200 cursor-not-allowed' 
                        : 'bg-white text-blue-600 border-blue-200 hover:bg-blue-50'}`}
                  >
                    {event.confirmedSlots >= event.maxVolunteers ? (
                      'Vagas Esgotadas'
                    ) : (
                      <>
                        <CheckCircle size={18} /> Quero Participar
                      </>
                    )}
                  </button>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="bg-gray-50 border border-gray-200 rounded-xl p-8 text-center text-gray-500">
            Nenhum evento agendado para os próximos dias.
          </div>
        )}
      </div>
    </div>
  );
};
