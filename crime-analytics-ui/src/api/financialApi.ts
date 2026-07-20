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
};
