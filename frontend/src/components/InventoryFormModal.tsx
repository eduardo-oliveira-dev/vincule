import React, { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../services/api';
import { X } from 'lucide-react';

interface Props {
  isOpen: boolean;
  onClose: () => void;
}

export const InventoryFormModal = ({ isOpen, onClose }: Props) => {
  const queryClient = useQueryClient();
  const [formData, setFormData] = useState({
    name: '',
    quantity: 0,
    unit: 'unidade',
    minimumStock: 10,
    category: 'Alimentos'
  });

  const mutation = useMutation({
    mutationFn: (newItem: typeof formData) => {
      return api.post('/inventory', newItem);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orgInventory'] });
      queryClient.invalidateQueries({ queryKey: ['criticalItems'] });
      onClose();
      setFormData({ name: '', quantity: 0, unit: 'unidade', minimumStock: 10, category: 'Alimentos' });
    }
  });

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-xl shadow-xl w-full max-w-md overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-100 flex justify-between items-center bg-gray-50">
          <h2 className="text-lg font-bold text-gray-800">Novo Item no Inventário</h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition-colors">
            <X size={20} />
          </button>
        </div>

        <form 
          className="p-6 space-y-4"
          onSubmit={(e) => {
            e.preventDefault();
            mutation.mutate(formData);
          }}
        >
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Nome do Item</label>
            <input 
              required
              type="text" 
              value={formData.name}
              onChange={e => setFormData({...formData, name: e.target.value})}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              placeholder="Ex: Arroz 5kg"
            />
          </div>
          
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Quantidade</label>
              <input 
                required
                type="number" 
                min="0"
                value={formData.quantity}
                onChange={e => setFormData({...formData, quantity: Number(e.target.value)})}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Estoque Mínimo</label>
              <input 
                required
                type="number" 
                min="0"
                value={formData.minimumStock}
                onChange={e => setFormData({...formData, minimumStock: Number(e.target.value)})}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Unidade</label>
              <select 
                value={formData.unit}
                onChange={e => setFormData({...formData, unit: e.target.value})}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="unidade">Unidade</option>
                <option value="kg">Kg</option>
                <option value="litro">Litro</option>
                <option value="pct">Pacote</option>
                <option value="caixa">Caixa</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Categoria</label>
              <select 
                value={formData.category}
                onChange={e => setFormData({...formData, category: e.target.value})}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              >
                <option value="Alimentos">Alimentos</option>
                <option value="Higiene">Higiene</option>
                <option value="Roupas">Roupas</option>
                <option value="Medicamentos">Medicamentos</option>
                <option value="Outros">Outros</option>
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
              className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 font-medium transition-colors disabled:opacity-50"
            >
              {mutation.isPending ? 'Salvando...' : 'Salvar Item'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
