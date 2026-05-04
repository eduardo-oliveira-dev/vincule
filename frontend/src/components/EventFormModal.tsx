import React, { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../services/api';
import { X } from 'lucide-react';

interface Props {
  isOpen: boolean;
  onClose: () => void;
}

export const EventFormModal = ({ isOpen, onClose }: Props) => {
  const queryClient = useQueryClient();
  const [error, setError] = useState('');
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    eventDate: '',
    maxVolunteers: 10,
    category: 'Geral'
  });

  const mutation = useMutation({
    mutationFn: (newEvent: typeof formData) => {
      const isoDate = formData.eventDate.length === 16 ? `${formData.eventDate}:00` : formData.eventDate;
      
      return api.post('/events', {
        ...newEvent,
        eventDate: isoDate
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orgEvents'] });
      queryClient.invalidateQueries({ queryKey: ['publicEvents'] });
      onClose();
      setError('');
      setFormData({ title: '', description: '', eventDate: '', maxVolunteers: 10, category: 'Geral' });
    },
    onError: (err: any) => {
      const msg = err.response?.data?.message || 'Erro ao criar evento. Verifique os campos e tente novamente.';
      setError(msg);
    }
  });

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-xl shadow-xl w-full max-w-md overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-100 flex justify-between items-center bg-gray-50">
          <h2 className="text-lg font-bold text-gray-800">Novo Evento de Voluntariado</h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition-colors">
            <X size={20} />
          </button>
        </div>

        <form 
          className="p-6 space-y-4"
          onSubmit={(e) => {
            e.preventDefault();
            setError('');
            mutation.mutate(formData);
          }}
        >
          {error && (
            <div className="bg-red-50 text-red-600 p-3 rounded-md text-sm">{error}</div>
          )}

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Título do Evento</label>
            <input 
              required
              type="text" 
              value={formData.title}
              onChange={e => setFormData({...formData, title: e.target.value})}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              placeholder="Ex: Mutirão de Limpeza"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Descrição</label>
            <textarea 
              required
              rows={3}
              value={formData.description}
              onChange={e => setFormData({...formData, description: e.target.value})}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500 resize-none"
              placeholder="Detalhes sobre as atividades e local..."
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Data e Hora</label>
            <input 
              required
              type="datetime-local" 
              value={formData.eventDate}
              onChange={e => setFormData({...formData, eventDate: e.target.value})}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Máx. Voluntários</label>
              <input 
                required
                type="number" 
                min="1"
                value={formData.maxVolunteers}
                onChange={e => setFormData({...formData, maxVolunteers: Number(e.target.value)})}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Categoria</label>
              <select 
                value={formData.category}
                onChange={e => setFormData({...formData, category: e.target.value})}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="Geral">Geral</option>
                <option value="Alimentação">Alimentação</option>
                <option value="Educação">Educação</option>
                <option value="Meio Ambiente">Meio Ambiente</option>
                <option value="Saúde">Saúde</option>
              </select>
            </div>
          </div>

          <div className="pt-4 flex space-x-3">
            <button 
              type="button" 
              onClick={onClose}
              className="flex-1 px-4 py-2 bg-white border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 font-medium transition-colors"
            >
              Cancelar
            </button>
            <button 
              type="submit" 
              disabled={mutation.isPending}
              className="flex-1 px-4 py-2 bg-purple-600 text-white rounded-md hover:bg-purple-700 font-medium transition-colors disabled:opacity-50"
            >
              {mutation.isPending ? 'Salvando...' : 'Criar Evento'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
