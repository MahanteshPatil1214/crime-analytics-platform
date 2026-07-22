import apiClient from './apiClient';
import { FinancialTransaction, FinancialSearchParams, FinancialStats } from '../types/financial';
import { PageResponse } from '../types/case';

export const financialApi = {
  search: (params: Record<string, any>) =>
    apiClient.get<PageResponse<FinancialTransaction>>('/api/v1/financial/search', {
      params,
    }).then((r) => r.data),

  getById: (id: string) =>
    apiClient.get<FinancialTransaction>(`/api/v1/financial/${id}`).then((r) => r.data),

  getStats: () =>
    apiClient.get<Record<string, number>>('/api/v1/financial/stats').then((r) => r.data),

  getFlagged: (page = 0, size = 20) =>
    apiClient.get<PageResponse<FinancialTransaction>>('/api/v1/financial/flagged', {
      params: { page, size },
    }).then((r) => r.data),

  getByCase: (caseId: number) =>
    apiClient.get<FinancialTransaction[]>(`/api/v1/financial/case/${caseId}`).then((r) => r.data),

  create: (data: Partial<FinancialTransaction>) =>
    apiClient.post<FinancialTransaction>('/api/v1/financial', data).then((r) => r.data),

  update: (id: string, data: Partial<FinancialTransaction>) =>
    apiClient.put<FinancialTransaction>(`/api/v1/financial/${id}`, data).then((r) => r.data),

  flag: (id: string, reason: string) =>
    apiClient.post<FinancialTransaction>(`/api/v1/financial/${id}/flag`, { reason }).then((r) => r.data),

  delete: (id: string) =>
    apiClient.delete(`/api/v1/financial/${id}`).then((r) => r.data),
};
