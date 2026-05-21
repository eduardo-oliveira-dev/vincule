import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../services/api';
import { Plus, Pencil, Trash2, AlertCircle, Search, Package } from 'lucide-react';
import { InventoryFormModal } from '../components/InventoryFormModal';

interface InventoryItem {
  id: string;
  name: string;
  quantity: number;
  minimumStock: number;
  unit: string;
  category: string;
  expiryDate: string | null;
  isCritical: boolean;
  createdAt: string;
}

export const Inventory = () => {
  const queryClient = useQueryClient();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState<InventoryItem | null>(null);
  const [search, setSearch] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');

  const { data: items, isLoading } = useQuery({
    queryKey: ['orgInventory'],
    queryFn: async () => {
      const { data } = await api.get<InventoryItem[]>('/inventory');
      return data;
    }
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => api.delete(`/inventory/${id}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orgInventory'] });
      queryClient.invalidateQueries({ queryKey: ['criticalItems'] });
    },
    onError: (error: any) => {
      alert(error.response?.data?.message || 'Erro ao remover item.');
    }
  });

  const handleDelete = (item: InventoryItem) => {
    if (confirm(`Confirma a remoção de "${item.name}"?`)) {
      deleteMutation.mutate(item.id);
    }
  };

  const handleEdit = (item: InventoryItem) => {
    setEditingItem(item);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setEditingItem(null);
  };

  const categories = [...new Set(items?.map(i => i.category) ?? [])];

  const filtered = items?.filter(item => {
    const matchSearch = item.name.toLowerCase().includes(search.toLowerCase());
    const matchCat = categoryFilter ? item.category === categoryFilter : true;
    return matchSearch && matchCat;
  }) ?? [];

  const isExpiringSoon = (date: string | null) => {
    if (!date) return false;
    const diff = (new Date(date).getTime() - Date.now()) / (1000 * 60 * 60 * 24);
    return diff <= 7 && diff >= 0;
  };

  return (
    <div className="space-y-6">
      <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">Gestão de Inventário</h1>
          <p className="text-gray-500 text-sm mt-1">Gerencie os itens em estoque da sua organização.</p>
        </div>
        <button
          onClick={() => setIsModalOpen(true)}
          className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg flex items-center gap-2 transition-colors"
        >
          <Plus size={18} /> Adicionar Item
        </button>
      </div>

      {/* Filtros */}
      <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-200 flex flex-col sm:flex-row gap-3">
        <div className="relative flex-1">
          <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input
            type="text"
            placeholder="Buscar por nome..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            className="w-full pl-9 pr-3 py-2 border border-gray-300 rounded-md text-sm focus:ring-blue-500 focus:border-blue-500"
          />
        </div>
        <select
          value={categoryFilter}
          onChange={e => setCategoryFilter(e.target.value)}
          className="px-3 py-2 border border-gray-300 rounded-md text-sm focus:ring-blue-500 focus:border-blue-500"
        >
          <option value="">Todas as categorias</option>
          {categories.map(c => <option key={c} value={c}>{c}</option>)}
        </select>
      </div>

      {/* Tabela */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
        {isLoading ? (
          <div className="p-8 text-center text-gray-500">Carregando inventário...</div>
        ) : filtered.length === 0 ? (
          <div className="p-12 text-center">
            <Package size={40} className="mx-auto text-gray-300 mb-3" />
            <p className="text-gray-500">Nenhum item encontrado.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  <th className="px-4 py-3 text-left font-semibold text-gray-600">Item</th>
                  <th className="px-4 py-3 text-left font-semibold text-gray-600">Categoria</th>
                  <th className="px-4 py-3 text-right font-semibold text-gray-600">Quantidade</th>
                  <th className="px-4 py-3 text-right font-semibold text-gray-600">Mínimo</th>
                  <th className="px-4 py-3 text-left font-semibold text-gray-600">Validade</th>
                  <th className="px-4 py-3 text-center font-semibold text-gray-600">Status</th>
                  <th className="px-4 py-3 text-center font-semibold text-gray-600">Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {filtered.map(item => (
                  <tr key={item.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-4 py-3 font-medium text-gray-900">{item.name}</td>
                    <td className="px-4 py-3 text-gray-500">{item.category}</td>
                    <td className={`px-4 py-3 text-right font-bold ${item.isCritical ? 'text-red-600' : 'text-gray-900'}`}>
                      {item.quantity} <span className="text-xs font-normal text-gray-400">{item.unit}</span>
                    </td>
                    <td className="px-4 py-3 text-right text-gray-500">{item.minimumStock}</td>
                    <td className="px-4 py-3">
                      {item.expiryDate ? (
                        <span className={`text-xs font-medium px-2 py-0.5 rounded-full ${isExpiringSoon(item.expiryDate) ? 'bg-orange-100 text-orange-700' : 'bg-gray-100 text-gray-600'}`}>
                          {new Date(item.expiryDate).toLocaleDateString('pt-BR')}
                        </span>
                      ) : (
                        <span className="text-gray-300 text-xs">—</span>
                      )}
                    </td>
                    <td className="px-4 py-3 text-center">
                      {item.isCritical ? (
                        <span className="inline-flex items-center gap-1 bg-red-100 text-red-700 text-xs font-bold px-2 py-0.5 rounded-full">
                          <AlertCircle size={12} /> Crítico
                        </span>
                      ) : (
                        <span className="bg-green-100 text-green-700 text-xs font-medium px-2 py-0.5 rounded-full">OK</span>
                      )}
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex justify-center gap-2">
                        <button
                          onClick={() => handleEdit(item)}
                          className="p-1.5 text-blue-600 hover:bg-blue-50 rounded transition-colors"
                          title="Editar"
                        >
                          <Pencil size={15} />
                        </button>
                        <button
                          onClick={() => handleDelete(item)}
                          disabled={deleteMutation.isPending}
                          className="p-1.5 text-red-500 hover:bg-red-50 rounded transition-colors disabled:opacity-50"
                          title="Remover"
                        >
                          <Trash2 size={15} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <InventoryFormModal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        item={editingItem}
      />
    </div>
  );
};
