import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { api } from '../services/api';
import { Plus, Calendar, Users, Clock } from 'lucide-react';
import { EventFormModal } from '../components/EventFormModal';

interface VolunteerEvent {
  id: string;
  title: string;
  description: string;
  eventDate: string;
  maxVolunteers: number;
  confirmedSlots: number;
  category: string;
}

export const Events = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const { data: events, isLoading } = useQuery({
    queryKey: ['orgEvents'],
    queryFn: async () => {
      const { data } = await api.get<VolunteerEvent[]>('/events');
      return data;
    }
  });

  const getCategoryColor = (category: string) => {
    const map: Record<string, string> = {
      Geral: 'bg-gray-100 text-gray-700',
      Alimentação: 'bg-orange-100 text-orange-700',
      Educação: 'bg-blue-100 text-blue-700',
      'Meio Ambiente': 'bg-green-100 text-green-700',
      Saúde: 'bg-pink-100 text-pink-700',
    };
    return map[category] ?? 'bg-purple-100 text-purple-700';
  };

  return (
    <div className="space-y-6">
      <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">Gestão de Eventos</h1>
          <p className="text-gray-500 text-sm mt-1">Crie e gerencie eventos de voluntariado da sua organização.</p>
        </div>
        <button
          onClick={() => setIsModalOpen(true)}
          className="bg-purple-600 hover:bg-purple-700 text-white font-medium py-2 px-4 rounded-lg flex items-center gap-2 transition-colors"
        >
          <Plus size={18} /> Criar Evento
        </button>
      </div>

      {isLoading ? (
        <div className="text-center text-gray-500 py-12">Carregando eventos...</div>
      ) : events && events.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {events.map(event => {
            const vagas = event.maxVolunteers - event.confirmedSlots;
            const pct = Math.round((event.confirmedSlots / event.maxVolunteers) * 100);
            return (
              <div key={event.id} className="bg-white rounded-xl shadow-sm border border-gray-200 flex flex-col overflow-hidden hover:shadow-md transition-shadow">
                <div className="p-5 flex-1">
                  <span className={`text-xs font-bold uppercase tracking-wider px-2 py-0.5 rounded-full ${getCategoryColor(event.category)}`}>
                    {event.category}
                  </span>
                  <h3 className="mt-3 text-lg font-bold text-gray-900 leading-tight">{event.title}</h3>
                  <p className="mt-1 text-sm text-gray-500 line-clamp-2">{event.description}</p>

                  <div className="mt-4 space-y-2 text-sm text-gray-500">
                    <div className="flex items-center gap-2">
                      <Calendar size={15} className="text-gray-400" />
                      <span>{new Date(event.eventDate).toLocaleString('pt-BR')}</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <Users size={15} className="text-gray-400" />
                      <span>{event.confirmedSlots}/{event.maxVolunteers} confirmados</span>
                    </div>
                  </div>

                  {/* Barra de progresso */}
                  <div className="mt-4">
                    <div className="flex justify-between text-xs text-gray-400 mb-1">
                      <span>Ocupação</span>
                      <span>{pct}%</span>
                    </div>
                    <div className="w-full bg-gray-100 rounded-full h-2">
                      <div
                        className={`h-2 rounded-full transition-all ${pct >= 100 ? 'bg-red-500' : pct >= 70 ? 'bg-yellow-500' : 'bg-green-500'}`}
                        style={{ width: `${Math.min(pct, 100)}%` }}
                      />
                    </div>
                  </div>
                </div>

                <div className="px-5 py-3 bg-gray-50 border-t border-gray-100 flex items-center gap-2 text-sm">
                  <Clock size={14} className="text-gray-400" />
                  <span className={`font-medium ${vagas <= 0 ? 'text-red-600' : 'text-green-600'}`}>
                    {vagas <= 0 ? 'Vagas esgotadas' : `${vagas} vaga${vagas > 1 ? 's' : ''} disponível`}
                  </span>
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        <div className="bg-white rounded-xl border border-gray-200 p-12 text-center">
          <Calendar size={40} className="mx-auto text-gray-300 mb-3" />
          <p className="text-gray-500">Nenhum evento futuro agendado.</p>
          <button
            onClick={() => setIsModalOpen(true)}
            className="mt-4 bg-purple-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-purple-700 transition-colors"
          >
            Criar o primeiro evento
          </button>
        </div>
      )}

      <EventFormModal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} />
    </div>
  );
};
