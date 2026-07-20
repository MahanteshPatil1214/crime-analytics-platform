import apiClient from './apiClient';
import { CaseMaster, CaseDetail, Involvement, CaseSearchResult, PageResponse } from '../types/case';

export const caseApi = {
  search: (params: Record<string, any>, page = 0, size = 20) =>
    apiClient.get<PageResponse<CaseSearchResult>>('/api/v1/cases/search', {
      params: { ...params, page, size },
    }).then((r) => r.data),

  getById: (id: number) =>
    apiClient.get<CaseDetail>(`/api/v1/cases/${id}`).then((r) => r.data),

  getInvolvements: (id: number) =>
    apiClient.get<Involvement[]>(`/api/v1/cases/${id}/involvements`).then((r) => r.data),

  getStats: () =>
    apiClient.get<Record<string, number>>('/api/v1/cases/stats').then((r) => r.data),

  getDistrictStats: () =>
    apiClient.get<{ unitId: number; count: number }[]>('/api/v1/cases/stats/districts').then((r) => r.data),

  getCrimeHeadStats: () =>
    apiClient.get<{ crimeHeadId: number; count: number }[]>('/api/v1/cases/stats/crime-heads').then((r) => r.data),
};
