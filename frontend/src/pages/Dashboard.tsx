import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { api } from '../services/api';
import { Package, Calendar, AlertCircle, Plus } from 'lucide-react';
import { InventoryFormModal } from '../components/InventoryFormModal';
import { EventFormModal } from '../components/EventFormModal';

interface InventoryItem {
  id: string;
  name: string;
  quantity: number;
  minimumStock: number;
  unit: string;
  category: string;
  isCritical: boolean;
  createdAt: string;
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

export const Dashboard = () => {
  const [isInventoryModalOpen, setIsInventoryModalOpen] = useState(false);
  const [isEventModalOpen, setIsEventModalOpen] = useState(false);

  const { data: items, isLoading: loadingItems } = useQuery({
    queryKey: ['orgInventory'],
    queryFn: async () => {
      const { data } = await api.get<InventoryItem[]>('/inventory');
      return data;
    }
  });

  const { data: events, isLoading: loadingEvents } = useQuery({
    queryKey: ['orgEvents'],
    queryFn: async () => {
      const { data } = await api.get<VolunteerEvent[]>('/events');
      return data;
    }
  });

  const criticalItems = items?.filter(item => item.isCritical) || [];

  return (
    <div className="space-y-6">
      <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
        <h1 className="text-2xl font-bold text-gray-800 mb-2">Painel da Organização</h1>
        <p className="text-gray-600">Visão geral do inventário e dos eventos cadastrados pela sua organização.</p>
      </div>

      {/* KPIs */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200 flex items-center space-x-4">
          <div className="p-3 bg-blue-100 text-blue-600 rounded-full">
            <Package size={24} />
          </div>
          <div>
            <p className="text-sm font-medium text-gray-500">Itens em Estoque</p>
            <p className="text-2xl font-bold text-gray-900">{loadingItems ? '...' : items?.length || 0}</p>
          </div>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200 flex items-center space-x-4">
          <div className="p-3 bg-purple-100 text-purple-600 rounded-full">
            <Calendar size={24} />
          </div>
          <div>
            <p className="text-sm font-medium text-gray-500">Eventos Agendados</p>
            <p className="text-2xl font-bold text-gray-900">{loadingEvents ? '...' : events?.length || 0}</p>
          </div>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-sm border border-red-200 flex items-center space-x-4">
          <div className="p-3 bg-red-100 text-red-600 rounded-full">
            <AlertCircle size={24} />
          </div>
          <div>
            <p className="text-sm font-medium text-red-500">Alertas Críticos</p>
            <p className="text-2xl font-bold text-red-600">{loadingItems ? '...' : criticalItems.length}</p>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Inventário */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
          <div className="px-6 py-4 border-b border-gray-200 bg-gray-50 flex justify-between items-center">
            <h2 className="text-lg font-semibold text-gray-800">Seu Inventário</h2>
            <button
              onClick={() => setIsInventoryModalOpen(true)}
              className="bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium py-1.5 px-3 rounded flex items-center gap-1 transition-colors"
            >
              <Plus size={16} /> Adicionar
            </button>
          </div>
          <div className="p-6">
            {loadingItems ? (
              <div className="text-center text-gray-500 py-4">Carregando itens...</div>
            ) : items && items.length > 0 ? (
              <div className="space-y-4 max-h-96 overflow-y-auto pr-2">
                {items.map(item => (
                  <div key={item.id} className="flex justify-between items-center border-b border-gray-100 pb-3 last:border-0 last:pb-0">
                    <div>
                      <p className="font-medium text-gray-900 flex items-center gap-2">
                        {item.name}
                        {item.isCritical && <span className="bg-red-100 text-red-600 text-xs px-2 py-0.5 rounded-full font-bold">CRÍTICO</span>}
                      </p>
                      <p className="text-sm text-gray-500">{item.category}</p>
                    </div>
                    <div className="text-right">
                      <p className={`font-bold ${item.isCritical ? 'text-red-600' : 'text-gray-900'}`}>
                        {item.quantity} <span className="text-xs text-gray-500 font-normal">{item.unit}</span>
                      </p>
                      <p className="text-xs text-gray-400">Min: {item.minimumStock}</p>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="text-center text-gray-500 py-8">Nenhum item cadastrado no inventário.</div>
            )}
          </div>
        </div>

        {/* Eventos */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
          <div className="px-6 py-4 border-b border-gray-200 bg-gray-50 flex justify-between items-center">
            <h2 className="text-lg font-semibold text-gray-800">Seus Eventos</h2>
            <button
              onClick={() => setIsEventModalOpen(true)}
              className="bg-purple-600 hover:bg-purple-700 text-white text-sm font-medium py-1.5 px-3 rounded flex items-center gap-1 transition-colors"
            >
              <Plus size={16} /> Criar Evento
            </button>
          </div>
          <div className="p-6">
            {loadingEvents ? (
              <div className="text-center text-gray-500 py-4">Carregando eventos...</div>
            ) : events && events.length > 0 ? (
              <div className="space-y-4 max-h-96 overflow-y-auto pr-2">
                {events.map(event => (
                  <div key={event.id} className="flex justify-between items-center border-b border-gray-100 pb-3 last:border-0 last:pb-0">
                    <div>
                      <p className="font-medium text-gray-900">{event.title}</p>
                      <p className="text-sm text-gray-500">{new Date(event.eventDate).toLocaleDateString('pt-BR')}</p>
                    </div>
                    <div className="text-right">
                      <p className="font-medium text-blue-600">
                        {event.confirmedSlots} / {event.maxVolunteers}
                      </p>
                      <p className="text-xs text-gray-500">vagas</p>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="text-center text-gray-500 py-8">Nenhum evento agendado.</div>
            )}
          </div>
        </div>
      </div>

      {/* Modais */}
      <InventoryFormModal
        isOpen={isInventoryModalOpen}
        onClose={() => setIsInventoryModalOpen(false)}
      />
      <EventFormModal
        isOpen={isEventModalOpen}
        onClose={() => setIsEventModalOpen(false)}
      />
    </div>
  );
};
